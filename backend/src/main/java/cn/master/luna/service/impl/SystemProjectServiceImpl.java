package cn.master.luna.service.impl;

import cn.master.luna.constants.*;
import cn.master.luna.entity.*;
import cn.master.luna.entity.dto.*;
import cn.master.luna.entity.request.*;
import cn.master.luna.exception.CustomException;
import cn.master.luna.mapper.SystemProjectMapper;
import cn.master.luna.mapper.SystemUserMapper;
import cn.master.luna.mapper.UserRoleRelationMapper;
import cn.master.luna.service.OperationLogService;
import cn.master.luna.service.SystemProjectService;
import cn.master.luna.service.UserLoginService;
import cn.master.luna.util.JacksonUtils;
import cn.master.luna.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.luna.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.luna.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject> implements SystemProjectService {
    private final OperationLogService operationLogService;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final SystemUserMapper systemUserMapper;
    private final UserLoginService userLoginService;

    private final static String PREFIX = "/project";
    private final static String ADD_PROJECT = PREFIX + "/add";
    private final static String UPDATE_PROJECT = PREFIX + "/update";
    private final static String REMOVE_PROJECT_MEMBER = PREFIX + "/remove-member/";
    private final static String ADD_MEMBER = PREFIX + "/add-member";

    @Override
    public Page<ProjectDTO> getProjectPage(OrganizationProjectRequest request) {
        Page<ProjectDTO> page = queryChain().select(SYSTEM_PROJECT.ALL_COLUMNS)
                .select(SYSTEM_ORGANIZATION.NAME.as("organization_name"))
                .from(SYSTEM_PROJECT)
                .innerJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
                .where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(request.getOrganizationId())
                        .and(SYSTEM_PROJECT.NAME.like(request.getKeyword())
                                .or(SYSTEM_PROJECT.NUM.like(request.getKeyword()))))
                .orderBy(SYSTEM_PROJECT.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ProjectDTO.class);
        buildUserInfo(page);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO add(SystemProject request, String createUser) {
        checkProjectExistByName(request);
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(request, projectDTO);
        projectDTO.setCreateUser(createUser);
        projectDTO.setUpdateUser(createUser);
        mapper.insert(projectDTO);
        ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
        memberRequest.setProjectIds(List.of(projectDTO.getId()));
        memberRequest.setUserIds(projectDTO.getUserIds());
        addProjectAdmin(memberRequest, createUser, ADD_PROJECT, OperationLogType.ADD.name(), "添加", OperationLogModule.SETTING_ORGANIZATION_PROJECT);
        return projectDTO;
    }

    private void addProjectAdmin(ProjectAddMemberBatchRequest request, String createUser, String path, String type, String content, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        request.getProjectIds().forEach(projectId -> {
            SystemProject project = mapper.selectOneById(projectId);
            Map<String, String> nameMap = addUserPre(request.getUserIds(), createUser, path, module, projectId, project);
            request.getUserIds().forEach(userId -> {
                boolean exists = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId))
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).exists();
                if (!exists) {
                    UserRoleRelation userRoleRelation = UserRoleRelation.builder()
                            .userId(userId).roleId(InternalUserRole.PROJECT_ADMIN.getValue())
                            .sourceId(projectId)
                            .createUser(createUser)
                            .organizationId(project.getOrganizationId())
                            .build();
                    userRoleRelationMapper.insert(userRoleRelation);
                    String logProjectId = OperationLogConstants.SYSTEM;
                    if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                        logProjectId = OperationLogConstants.ORGANIZATION;
                    }

                    LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), userRoleRelation.getId(), createUser, type, module, content + "项目管理员" + ": " + nameMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
        });
        operationLogService.batchAdd(logDTOList);
    }

    private Map<String, String> addUserPre(List<String> userIds, String createUser, String path, String module, String projectId, SystemProject project) {
        checkProjectNotExist(projectId);
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.in(userIds)).list();
        if (userIds.size() != users.size()) {
            throw new CustomException(Translator.get("user_not_exist"));
        }
        //把id和名称放一个map中
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(SystemUser::getId, SystemUser::getName));
        checkOrgRoleExit(userIds, project.getOrganizationId(), createUser, userMap, path, module);
        return userMap;
    }

    private void checkOrgRoleExit(List<String> userId, String orgId, String createUser, Map<String, String> nameMap, String path, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.eq(userId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(orgId))).list();
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        if (!userId.isEmpty()) {
            userId.forEach(id -> {
                if (!orgUserIds.contains(id)) {
                    UserRoleRelation memberRole = UserRoleRelation.builder().build();
                    memberRole.setUserId(id);
                    memberRole.setRoleId(InternalUserRole.ORG_MEMBER.getValue());
                    memberRole.setSourceId(orgId);
                    memberRole.setCreateUser(createUser);
                    memberRole.setOrganizationId(orgId);
                    userRoleRelationMapper.insert(memberRole);
                    LogDTO logDTO = new LogDTO(orgId, orgId, memberRole.getId(), createUser, OperationLogType.ADD.name(), module, "添加组织-成员: " + nameMap.get(id));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
        }
        operationLogService.batchAdd(logDTOList);
    }

    private void setLog(LogDTO dto, String path, String method, List<LogDTO> logDTOList) {
        dto.setPath(path);
        dto.setMethod(method);
        dto.setOriginalValue(JacksonUtils.toJSONBytes(StringUtils.EMPTY));
        logDTOList.add(dto);
    }

    private void checkProjectNotExist(String projectId) {
        if (mapper.selectOneById(projectId) == null) {
            throw new CustomException("<<项目不存在>>");
        }
    }

    private void checkProjectExistByName(SystemProject project) {
        boolean exists = queryChain().where(SYSTEM_PROJECT.NAME.eq(project.getName())
                .and(SYSTEM_PROJECT.ORGANIZATION_ID.eq(project.getOrganizationId()))
                .and(SYSTEM_PROJECT.ID.ne(project.getId()))).exists();
        if (exists) {
            throw new CustomException("<项目名称已存在>");
        }
    }

    @Override
    public List<SystemProject> getUserProject(String organizationId, String userId) {
        checkOrg(organizationId);
        SystemUser user = QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userId).one();
        String projectId;
        if (user != null && StringUtils.isNotBlank(user.getLastProjectId())) {
            projectId = user.getLastProjectId();
        } else {
            projectId = null;
        }
        //判断用户是否是系统管理员
        List<SystemProject> allProject;
        boolean exists = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ADMIN.name()))).exists();
        if (exists) {
            allProject = queryChain().where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)
                    .and(SYSTEM_PROJECT.ENABLE.eq(true))
            ).orderBy(SYSTEM_PROJECT.NAME.desc()).list();
        } else {
            allProject = queryChain().select(QueryMethods.distinct(SYSTEM_PROJECT.ALL_COLUMNS))
                    .from(SYSTEM_USER_ROLE)
                    .join(USER_ROLE_RELATION).on(SYSTEM_USER_ROLE.ID.eq(USER_ROLE_RELATION.ROLE_ID))
                    .join(SYSTEM_PROJECT).on(SYSTEM_PROJECT.ID.eq(USER_ROLE_RELATION.SOURCE_ID))
                    .join(SYSTEM_USER).on(SYSTEM_USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                    .where(USER_ROLE_RELATION.USER_ID.eq(userId)
                            .and(SYSTEM_USER_ROLE.TYPE.eq("PROJECT"))
                            .and(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)).and(SYSTEM_PROJECT.ENABLE.eq(true)))
                    .orderBy(SYSTEM_PROJECT.NAME.desc())
                    .list();
        }
        List<SystemProject> temp = allProject;
        return allProject.stream()
                .filter(project -> Strings.CS.equals(project.getId(), projectId))
                .findFirst()
                .map(project -> {
                    temp.remove(project);
                    temp.addFirst(project);
                    return temp;
                })
                .orElse(allProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO updateProject(SystemProject systemProject, String updateUser) {
        ProjectDTO projectDTO = new ProjectDTO();
        checkProjectExistByName(systemProject);
        checkProjectNotExist(systemProject.getId());
        BeanUtils.copyProperties(systemProject, projectDTO);
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(systemProject.getId())
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).list();
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        List<LogDTO> logDTOList = new ArrayList<>();
        List<String> deleteIds = orgUserIds.stream()
                .filter(item -> !systemProject.getUserIds().contains(item))
                .toList();
        List<String> insertIds = systemProject.getUserIds().stream()
                .filter(item -> !orgUserIds.contains(item))
                .toList();
        if (!deleteIds.isEmpty()) {
            QueryChain<UserRoleRelation> chain = QueryChain.of(UserRoleRelation.class)
                    .where(USER_ROLE_RELATION.SOURCE_ID.eq(systemProject.getId())
                            .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))
                            .and(USER_ROLE_RELATION.USER_ID.in(deleteIds)));
            chain.list().forEach(role -> {
                SystemUser user = QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(role.getUserId()).one();
                String logProjectId = OperationLogConstants.SYSTEM;
                if (Strings.CS.equals(OperationLogModule.SETTING_ORGANIZATION_PROJECT, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                    logProjectId = OperationLogConstants.ORGANIZATION;
                }
                LogDTO logDTO = new LogDTO(logProjectId, systemProject.getOrganizationId(), role.getId(), updateUser, OperationLogType.DELETE.name(), OperationLogModule.SETTING_ORGANIZATION_PROJECT, "删除项目管理员: " + user.getName());
                setLog(logDTO, UPDATE_PROJECT, HttpMethodConstants.POST.name(), logDTOList);
            });
            userRoleRelationMapper.deleteByQuery(chain);
        }
        if (!insertIds.isEmpty()) {
            ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
            memberRequest.setProjectIds(List.of(systemProject.getId()));
            memberRequest.setUserIds(insertIds);
            addProjectAdmin(memberRequest, updateUser, UPDATE_PROJECT, OperationLogType.ADD.name(), "添加", OperationLogModule.SETTING_ORGANIZATION_PROJECT);
        }
        if (!logDTOList.isEmpty()) {
            operationLogService.batchAdd(logDTOList);
        }
        mapper.update(projectDTO);
        return projectDTO;
    }

    @Override
    public List<UserExtendDTO> getUserAdminList(String organizationId, String keyword) {
        checkOrg(organizationId);
        return QueryChain.of(SystemUser.class)
                .select(QueryMethods.distinct(SystemUser::getId, SystemUser::getName, SystemUser::getEmail, SystemUser::getCreateTime))
                .from(SystemUser.class)
                .leftJoin(USER_ROLE_RELATION).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.ORGANIZATION_ID.eq(organizationId)
                        .and(SYSTEM_USER.NAME.like(keyword).or(SYSTEM_USER.EMAIL.like(keyword))))
                .orderBy(SYSTEM_USER.CREATE_TIME.desc()).limit(1000)
                .listAs(UserExtendDTO.class);
    }

    @Override
    public UserDTO switchProject(ProjectSwitchRequest request, String currentUserId) {
        if (!Strings.CS.equals(currentUserId, request.getUserId())) {
            throw new CustomException("未经授权");
        }
        if (mapper.selectOneById(request.getProjectId()) == null) {
            throw new CustomException("<项目不存在>");
        }
        SystemUser build = SystemUser.builder().id(request.getUserId()).lastProjectId(request.getProjectId()).build();
        systemUserMapper.update(build);
        return userLoginService.getUserDTO(request.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(String id, String userName) {
        checkProjectNotExist(id);
        SystemProject project = new SystemProject();
        project.setId(id);
        project.setEnable(true);
        project.setUpdateUser(userName);
        mapper.update(project);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(String id, String userName) {
        checkProjectNotExist(id);
        SystemProject project = new SystemProject();
        project.setId(id);
        project.setEnable(false);
        project.setUpdateUser(userName);
        mapper.update(project);
    }

    @Override
    public Page<UserExtendDTO> getProjectMember(ProjectMemberRequest request) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select(SYSTEM_USER.ALL_COLUMNS, USER_ROLE_RELATION.ROLE_ID, USER_ROLE_RELATION.CREATE_TIME.as("memberTime"))
                .from(USER_ROLE_RELATION)
                .leftJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getProjectId()))
                .and(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc());
        Page<UserExtendDTO> page = queryChain()
                .select("temp.id,temp.name,temp.email,temp.phone")
                .select("MAX( if (temp.role_id = 'project_admin', true, false)) as adminFlag")
                .select("MIN(temp.memberTime) as groupTime")
                .from(queryWrapper).as("temp")
                .groupBy("temp.id")
                .orderBy("adminFlag desc", "groupTime desc")
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserExtendDTO.class);
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<String> userIds = page.getRecords().stream().map(UserExtendDTO::getId).toList();
            List<UserRoleOptionDto> userRole = selectProjectUserRoleByUserIds(userIds, request.getProjectId());
            Map<String, List<UserRoleOptionDto>> roleMap = userRole.stream().collect(Collectors.groupingBy(UserRoleOptionDto::getUserId));
            page.getRecords().forEach(user -> {
                if (roleMap.containsKey(user.getId())) {
                    user.setUserRoleList(roleMap.get(user.getId()));
                }
            });
        }
        return page;
    }

    @Override
    public List<UserExtendDTO> getMemberOption(String projectId, String keyword) {
        SystemProject project = mapper.selectOneById(projectId);
        if (project == null) {
            return new ArrayList<>();
        }
        // 组织成员
        List<UserExtendDTO> orgMembers = getMemberByOrg(project.getOrganizationId(), keyword);
        if (CollectionUtils.isEmpty(orgMembers)) {
            return new ArrayList<>();
        }
        // 设置是否是项目成员
        List<String> orgMemberIds = orgMembers.stream().map(UserExtendDTO::getId).toList();
        List<UserRoleRelation> projectRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.in(orgMemberIds)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId))
                        .and(USER_ROLE_RELATION.ORGANIZATION_ID.eq(project.getOrganizationId())))
                .list();
        if (CollectionUtils.isEmpty(projectRelations)) {
            orgMembers.forEach(orgMember -> orgMember.setMemberFlag(false));
        } else {
            List<String> projectUsers = projectRelations.stream().map(UserRoleRelation::getUserId).distinct().toList();
            // 已经是项目成员的组织成员, 禁用
            orgMembers.forEach(orgMember -> orgMember.setMemberFlag(projectUsers.contains(orgMember.getId())));
        }
        return orgMembers;
    }

    @Override
    public List<OptionDTO> getRoleOption(String projectId) {
        List<SystemUserRole> userRoles = QueryChain.of(SystemUserRole.class)
                .where(SYSTEM_USER_ROLE.TYPE.eq(UserRoleType.PROJECT.name())
                        .and(SYSTEM_USER_ROLE.SCOPE_ID.in(Arrays.asList(projectId, "global"))))
                .list();
        return userRoles.stream().map(userRole -> new OptionDTO(userRole.getId(), userRole.getName())).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeProjectMember(String projectId, String userId, String createUser) {
        checkProjectNotExist(projectId);
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.eq(userId)).list();
        SystemUser user = CollectionUtils.isNotEmpty(users) ? users.getFirst() : null;
        if (user == null) {
            throw new CustomException(Translator.get("user_not_exist"));
        }
        //判断用户是不是最后一个管理员  如果是  就报错
        boolean exists = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.ne(user.getId())
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId))
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).exists();
        if (!exists) {
            throw new CustomException(Translator.get("keep_at_least_one_administrator"));
        }
        if (Strings.CS.equals(projectId, user.getLastProjectId())) {
            user.setLastProjectId(StringUtils.EMPTY);
            systemUserMapper.update(user);
        }
        List<LogDTO> logDTOList = new ArrayList<>();
        QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(user.getId())
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)));
        queryChain.list().forEach(userRoleRelation -> {
            String logProjectId = OperationLogConstants.SYSTEM;
            if (Strings.CS.equals(OperationLogModule.SETTING_ORGANIZATION_PROJECT, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                logProjectId = OperationLogConstants.ORGANIZATION;
            }
            LogDTO logDTO = new LogDTO(logProjectId, OperationLogConstants.SYSTEM, userRoleRelation.getId(), createUser, OperationLogType.DELETE.name(), OperationLogModule.SETTING_ORGANIZATION_PROJECT, Translator.get("delete") + Translator.get("project_member") + ": " + user.getName());
            setLog(logDTO, StringUtils.join(REMOVE_PROJECT_MEMBER, projectId, "/", userId), HttpMethodConstants.GET.name(), logDTOList);
        });
        operationLogService.batchAdd(logDTOList);
        return userRoleRelationMapper.deleteByQuery(queryChain);
    }

    @Override
    public Page<UserExtendDTO> getMemberList(ProjectUserRequest request) {
        checkOrgIsExist(request.getOrganizationId());
        checkProjectNotExist(request.getProjectId());
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())).list();
        List<String> userIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).distinct().toList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.select(USER_ROLE_RELATION.ALL_COLUMNS).from(USER_ROLE_RELATION)
                    .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getProjectId()));
            return QueryChain.of(SystemUser.class)
                    .select(QueryMethods.distinct(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL))
                    .select("count(temp.id) > 0 as memberFlag")
                    .from(SYSTEM_USER).as("u")
                    .leftJoin(queryWrapper).as("temp").on("temp.user_id = u.id")
                    .where(SYSTEM_USER.ID.in(userIds)
                            .and(SYSTEM_USER.NAME.like(request.getKeyword()).or(SYSTEM_USER.EMAIL.like(request.getKeyword()))))
                    .groupBy(SYSTEM_USER.ID)
                    .orderBy(SYSTEM_USER.CREATE_TIME.desc())
                    .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserExtendDTO.class);
        }
        return new Page<>();
    }

    @Override
    public void orgAddProjectMember(ProjectAddMemberRequest request, String createUser) {
        addProjectUser(request, createUser, ADD_MEMBER, OperationLogType.ADD.name(), Translator.get("add"), OperationLogModule.SETTING_ORGANIZATION_PROJECT);
    }

    @Override
    public void addMemberByProject(ProjectAddMemberRequest request, String createUser) {
        addProjectUser(request, createUser, ADD_MEMBER, OperationLogType.ADD.name(), Translator.get("add"), OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }

    private void addProjectUser(ProjectAddMemberRequest request, String createUser, String path, String type, String content, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        SystemProject project = mapper.selectOneById(request.getProjectId());
        Map<String, String> userMap = addUserPre(request.getUserIds(), createUser, path, module, request.getProjectId(), project);
        request.getUserIds().forEach(userId -> {
            request.getUserRoleIds().forEach(userRoleId -> {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(userId);
                userRoleRelation.setSourceId(request.getProjectId());
                userRoleRelation.setRoleId(userRoleId);
                userRoleRelation.setCreateUser(createUser);
                userRoleRelation.setOrganizationId(project.getOrganizationId());
                userRoleRelations.add(userRoleRelation);
                String logProjectId = OperationLogConstants.SYSTEM;
                if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                    logProjectId = OperationLogConstants.ORGANIZATION;
                }
                LogDTO logDTO = new LogDTO(logProjectId, OperationLogConstants.SYSTEM, userRoleRelation.getId(), createUser, type, module, content + Translator.get("project_member") + ": " + userMap.get(userId));
                setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
            });
        });
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
        operationLogService.batchAdd(logDTOList);
    }

    private void checkOrgIsExist(@NotBlank(message = "{organization.id.not_blank}") String organizationId) {
        if (!QueryChain.of(SystemOrganization.class).where(SystemOrganization::getId).eq(organizationId).exists()) {
            throw new CustomException(Translator.get("organization_not_exists"));
        }
    }

    private List<UserExtendDTO> getMemberByOrg(String organizationId, String keyword) {
        return QueryChain.of(UserRoleRelation.class)
                .select(QueryMethods.distinct(SYSTEM_USER.ALL_COLUMNS))
                .from(USER_ROLE_RELATION).join(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId))
                .and(SYSTEM_USER.NAME.like(keyword).or(SYSTEM_USER.EMAIL.like(keyword)))
                .orderBy(SYSTEM_USER.NAME.desc()).limit(1000)
                .listAs(UserExtendDTO.class);
    }

    private List<UserRoleOptionDto> selectProjectUserRoleByUserIds(List<String> userIds, String projectId) {
        return QueryChain.of(UserRoleRelation.class)
                .select(QueryMethods.distinct(USER_ROLE_RELATION.ROLE_ID.as("id"), SYSTEM_USER_ROLE.NAME, USER_ROLE_RELATION.USER_ID))
                .from(USER_ROLE_RELATION)
                .leftJoin(SYSTEM_USER_ROLE).on(USER_ROLE_RELATION.ROLE_ID.eq(SYSTEM_USER_ROLE.ID))
                .where(USER_ROLE_RELATION.USER_ID.in(userIds))
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)).listAs(UserRoleOptionDto.class);
    }

    private void checkOrg(String organizationId) {
        QueryChain.of(SystemOrganization.class).where(SYSTEM_ORGANIZATION.ID.eq(organizationId))
                .oneOpt().orElseThrow(() -> new CustomException("<组织不存在>"));
    }

    private void buildUserInfo(Page<ProjectDTO> page) {
        List<ProjectDTO> projectList = page.getRecords();
        // 获取项目id
        List<String> projectIds = projectList.stream().map(ProjectDTO::getId).toList();
        List<ProjectDTO> projectDTOList = getProjectExtendDTOList(projectIds);
        Map<String, ProjectDTO> projectMap = projectDTOList.stream().collect(Collectors.toMap(ProjectDTO::getId, projectDTO -> projectDTO));
        projectList.forEach(projectDTO -> {
            projectDTO.setMemberCount(projectMap.get(projectDTO.getId()).getMemberCount());
        });
        page.setRecords(projectList);
    }

    private List<ProjectDTO> getProjectExtendDTOList(List<String> projectIds) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select(USER_ROLE_RELATION.SOURCE_ID, SYSTEM_USER.ID)
                .from(USER_ROLE_RELATION)
                .leftJoin(SYSTEM_USER).on(SYSTEM_USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds));
        return queryChain().select(SYSTEM_PROJECT.ID)
                .select("count(distinct temp.id) as memberCount")
                .from(SYSTEM_PROJECT.as("p"))
                .leftJoin(queryWrapper).as("temp").on("p.id = temp.source_id")
                .groupBy(SYSTEM_PROJECT.ID).listAs(ProjectDTO.class);
    }
}
