package cn.master.luna.service.impl;

import cn.master.luna.constants.UserRoleType;
import cn.master.luna.controller.AuthController;
import cn.master.luna.entity.*;
import cn.master.luna.entity.dto.UserDTO;
import cn.master.luna.entity.dto.UserRolePermissionDTO;
import cn.master.luna.entity.dto.UserRoleResourceDTO;
import cn.master.luna.exception.CustomException;
import cn.master.luna.handler.ResultHandler;
import cn.master.luna.mapper.SystemUserMapper;
import cn.master.luna.service.UserLoginService;
import cn.master.luna.util.JwtTokenUtil;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.luna.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.luna.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.luna.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * @author Created by 11's papa on 2025/7/8
 */
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {
    private final AuthenticationManager authenticationManager;
    private final SystemUserMapper systemUserMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public ResultHandler login(AuthController.LoginRequest request) {
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        Authentication authentication = authenticationManager.authenticate(authenticationRequest);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        String tokenValue = jwtTokenUtil.generateToken(request.username());
        UserDTO userDTO = QueryChain.of(SystemUser.class).where(SystemUser::getName).eq(request.username()).oneAs(UserDTO.class);
        autoSwitch(userDTO);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("access_token", tokenValue);
        result.put("userInfo", getUserDTO(userDTO.getId()));
        return ResultHandler.success(result);
    }

    @Override
    public void updateUser(SystemUser user) {
        if (StringUtils.isNotBlank(user.getEmail())) {
            boolean exists = QueryChain.of(SystemUser.class).where(SYSTEM_USER.EMAIL.eq(user.getEmail())
                    .and(SYSTEM_USER.ID.ne(user.getId()))).exists();
            if (exists) {
                throw new CustomException("用户邮箱已存在");
            }
        }
        SystemUser userFromDB = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.eq(user.getId())).one();
        // last organization id 变了
        if (user.getLastOrganizationId() != null
                && !StringUtils.equals(user.getLastOrganizationId(), userFromDB.getLastOrganizationId())
                && !isSuperUser(user.getId())) {
            List<SystemProject> projects = getProjectListByWsAndUserId(user.getId(), user.getLastOrganizationId());
            if (!projects.isEmpty()) {
                // 如果传入的 last_project_id 是 last_organization_id 下面的
                boolean present = projects.stream().anyMatch(p -> StringUtils.equals(p.getId(), user.getLastProjectId()));
                if (!present) {
                    user.setLastProjectId(projects.getFirst().getId());
                }
            } else {
                user.setLastProjectId(StringUtils.EMPTY);
            }
        }
        systemUserMapper.update(user);
    }

    @Override
    public UserDTO getUserDTO(String userId) {
        UserDTO userDTO = systemUserMapper.selectOneWithRelationsByIdAs(userId, UserDTO.class);
        if (userDTO == null) {
            return null;
        }
        if (BooleanUtils.isFalse(userDTO.getEnable())) {
            throw new DisabledException("<UNK>");
        }
        UserRolePermissionDTO dto = getUserRolePermission(userId);
        userDTO.setUserRoleRelations(dto.getUserRoleRelations());
        userDTO.setUserRoles(dto.getUserRoles());
        userDTO.setUserRolePermissions(dto.getList());
        return userDTO;
    }

    private UserRolePermissionDTO getUserRolePermission(String userId) {
        UserRolePermissionDTO permissionDTO = new UserRolePermissionDTO();
        List<UserRoleResourceDTO> list = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)).list();
        if (userRoleRelations.isEmpty()) {
            return permissionDTO;
        }
        permissionDTO.setUserRoleRelations(userRoleRelations);
        List<String> roleList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).collect(Collectors.toList());
        List<SystemUserRole> userRoles = QueryChain.of(SystemUserRole.class).where(SystemUserRole::getId).in(roleList).list();
        permissionDTO.setUserRoles(userRoles);
        for (SystemUserRole gp : userRoles) {
            UserRoleResourceDTO dto = new UserRoleResourceDTO();
            dto.setUserRole(gp);
            List<UserRolePermission> userRolePermissions = QueryChain.of(UserRolePermission.class).where(UserRolePermission::getRoleId).eq(gp.getId()).list();
            dto.setUserRolePermissions(userRolePermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }

    private List<SystemProject> getProjectListByWsAndUserId(String userId, String organizationId) {
        List<SystemProject> projects = QueryChain.of(SystemProject.class)
                .where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId).and(SYSTEM_PROJECT.ENABLE.eq(true)))
                .list();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)).list();
        List<SystemProject> projectList = new ArrayList<>();
        userRoleRelations.forEach(userRoleRelation -> projects.forEach(project -> {
            if (StringUtils.equals(userRoleRelation.getSourceId(), project.getId()) && !projectList.contains(project)) {
                projectList.add(project);
            }

        }));
        return projectList;
    }

    private void autoSwitch(UserDTO user) {
        // 判断是否是系统管理员
        if (isSystemAdmin(user)) {
            return;
        }
        // 用户有 last_project_id 权限
        if (hasLastProjectPermission(user)) {
            return;
        }
        // 用户有 last_organization_id 权限
        if (hasLastOrganizationPermission(user)) {
            return;
        }
        // 判断其他权限
        checkNewOrganizationAndProject(user);
    }

    private void checkNewOrganizationAndProject(UserDTO user) {
        List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations();
        List<String> projectRoleIds = user.getUserRoles()
                .stream().filter(ug -> StringUtils.equals(ug.getType(), UserRoleType.PROJECT.name()))
                .map(SystemUserRole::getId)
                .toList();
        List<UserRoleRelation> project = userRoleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                .toList();
        if (project.isEmpty()) {
            List<String> organizationIds = user.getUserRoles()
                    .stream()
                    .filter(ug -> StringUtils.equals(ug.getType(), UserRoleType.ORGANIZATION.name()))
                    .map(SystemUserRole::getId)
                    .toList();
            List<UserRoleRelation> organizations = userRoleRelations.stream().filter(ug -> organizationIds.contains(ug.getRoleId()))
                    .toList();
            if (!organizations.isEmpty()) {
                //获取所有的组织
                List<String> orgIds = organizations.stream().map(UserRoleRelation::getSourceId).toList();
                List<SystemOrganization> organizationsList = QueryChain.of(SystemOrganization.class).where(SYSTEM_ORGANIZATION.ID.in(orgIds)
                        .and(SYSTEM_ORGANIZATION.ENABLE.eq(true))).list();
                if (!organizationsList.isEmpty()) {
                    String wsId = organizationsList.getFirst().getId();
                    switchUserResource(wsId, user);
                }
            } else {
                // 用户登录之后没有项目和组织的权限就把值清空
                user.setLastOrganizationId(StringUtils.EMPTY);
                user.setLastProjectId(StringUtils.EMPTY);
                updateUser(user);
            }
        } else {
            UserRoleRelation userRoleRelation = project.stream().filter(p -> StringUtils.isNotBlank(p.getSourceId()))
                    .toList().getFirst();
            String projectId = userRoleRelation.getSourceId();
            SystemProject systemProject = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ID.eq(projectId)).one();
            String wsId = systemProject.getOrganizationId();
            user.setId(user.getId());
            user.setLastProjectId(projectId);
            user.setLastOrganizationId(wsId);
            updateUser(user);
        }
    }

    private void switchUserResource(String sourceId, UserDTO sessionUser) {
        UserDTO user = getUserDTO(sessionUser.getId());
        SystemUser newUser = new SystemUser();
        user.setLastOrganizationId(sourceId);
        sessionUser.setLastOrganizationId(sourceId);
        user.setLastProjectId(StringUtils.EMPTY);
        List<SystemProject> projects = getProjectListByWsAndUserId(sessionUser.getId(), sourceId);
        if (!projects.isEmpty()) {
            user.setLastProjectId(projects.getFirst().getId());
        }
        BeanUtils.copyProperties(user, newUser);
        systemUserMapper.update(newUser);
    }

    private boolean hasLastOrganizationPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastOrganizationId())) {
            List<SystemOrganization> organizations = QueryChain.of(SystemOrganization.class)
                    .where(SYSTEM_ORGANIZATION.ID.eq(user.getLastOrganizationId())
                            .and(SYSTEM_ORGANIZATION.ENABLE.eq(true))).list();
            if (organizations.isEmpty()) {
                return false;
            }
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> StringUtils.equals(user.getLastOrganizationId(), ug.getSourceId()))
                    .toList();
            if (!userRoleRelations.isEmpty()) {
                List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(user.getLastOrganizationId()).and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
                if (projects.isEmpty()) {
                    user.setLastProjectId(StringUtils.EMPTY);
                    updateUser(user);
                    return true;
                }
                // 组织下有项目，选中有权限的项目
                List<String> projectIds = projects.stream()
                        .map(SystemProject::getId)
                        .toList();

                List<UserRoleRelation> roleRelations = user.getUserRoleRelations();
                List<String> projectRoleIds = user.getUserRoles()
                        .stream().filter(ug -> StringUtils.equals(ug.getType(), UserRoleType.PROJECT.name()))
                        .map(SystemUserRole::getId)
                        .toList();
                List<String> projectIdsWithPermission = roleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                        .map(UserRoleRelation::getSourceId)
                        .filter(StringUtils::isNotBlank)
                        .filter(projectIds::contains)
                        .toList();

                List<String> intersection = projectIds.stream().filter(projectIdsWithPermission::contains).toList();
                if (intersection.isEmpty()) {
                    user.setLastProjectId(StringUtils.EMPTY);
                    updateUser(user);
                    return true;
                }
                Optional<SystemProject> first = projects.stream().filter(p -> StringUtils.equals(intersection.getFirst(), p.getId())).findFirst();
                if (first.isPresent()) {
                    SystemProject project = first.get();
                    String wsId = project.getOrganizationId();
                    user.setId(user.getId());
                    user.setLastProjectId(project.getId());
                    user.setLastOrganizationId(wsId);
                    updateUser(user);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasLastProjectPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastProjectId())) {
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> StringUtils.equals(user.getLastProjectId(), ug.getSourceId()))
                    .toList();
            if (!userRoleRelations.isEmpty()) {
                List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ID.eq(user.getLastProjectId()).and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
                if (!projects.isEmpty()) {
                    SystemProject project = projects.getFirst();
                    if (StringUtils.equals(project.getOrganizationId(), user.getLastOrganizationId())) {
                        return true;
                    }
                    // last_project_id 和 last_organization_id 对应不上了
                    user.setLastOrganizationId(project.getOrganizationId());
                    updateUser(user);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSystemAdmin(UserDTO user) {
        if (isSuperUser(user.getId())) {
            // 如果是系统管理员，判断是否有项目权限
            if (StringUtils.isNotBlank(user.getLastProjectId())) {
                List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ID.eq(user.getLastProjectId()).and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
                if (!projects.isEmpty()) {
                    SystemProject project = projects.getFirst();
                    if (StringUtils.equals(project.getOrganizationId(), user.getLastOrganizationId())) {
                        return true;
                    }
                    // last_project_id 和 last_organization_id 对应不上了
                    user.setLastOrganizationId(project.getOrganizationId());
                    updateUser(user);
                    return true;
                }
            }
            // 项目没有权限  则取当前组织下的第一个项目
            if (StringUtils.isNotBlank(user.getLastOrganizationId())) {
                List<SystemOrganization> organizations = QueryChain.of(SystemOrganization.class)
                        .where(SYSTEM_ORGANIZATION.ID.eq(user.getLastOrganizationId())
                                .and(SYSTEM_ORGANIZATION.ENABLE.eq(true))).list();
                if (!organizations.isEmpty()) {
                    SystemOrganization organization = organizations.getFirst();
                    List<SystemProject> projectList = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organization.getId())
                            .and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
                    if (!projectList.isEmpty()) {
                        SystemProject project = projectList.getFirst();
                        user.setLastProjectId(project.getId());
                    }
                    updateUser(user);
                    return true;
                }
            }
            //项目和组织都没有权限
            SystemProject project = QueryChain.of(SystemProject.class)
                    .select(SYSTEM_PROJECT.ALL_COLUMNS)
                    .from(SYSTEM_PROJECT)
                    .leftJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
                    .where(SYSTEM_PROJECT.ENABLE.eq(1).and(SYSTEM_ORGANIZATION.ENABLE.eq(true)))
                    .limit(1)
                    .one();
            if (project != null) {
                user.setLastProjectId(project.getId());
                user.setLastOrganizationId(project.getOrganizationId());
                this.updateUser(user);
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean isSuperUser(String id) {
        return QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(id)
                .and(USER_ROLE_RELATION.ROLE_ID.eq("admin"))).exists();
    }
}
