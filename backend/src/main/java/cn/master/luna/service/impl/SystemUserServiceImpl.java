package cn.master.luna.service.impl;

import cn.master.luna.constants.UserRoleScope;
import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.entity.dto.BasePageRequest;
import cn.master.luna.entity.dto.UserTableResponse;
import cn.master.luna.entity.request.AddUserRequest;
import cn.master.luna.exception.CustomException;
import cn.master.luna.mapper.SystemUserMapper;
import cn.master.luna.mapper.UserRoleRelationMapper;
import cn.master.luna.service.SystemUserService;
import cn.master.luna.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-01
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRelationMapper userRoleRelationMapper;

    @Override
    public Page<UserTableResponse> getUserPage(BasePageRequest request) {
        Page<UserTableResponse> page = queryChain()
                .where(SYSTEM_USER.ID.eq(request.getKeyword())
                        .or(SYSTEM_USER.NAME.eq(request.getKeyword()))
                        .or(SYSTEM_USER.EMAIL.eq(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.eq(request.getKeyword())))
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserTableResponse.class);
        if (!page.getRecords().isEmpty()) {
            List<String> userIds = page.getRecords().stream().map(SystemUser::getId).toList();
            Map<String, UserTableResponse> roleAndOrganizationMap = selectGlobalUserRoleAndOrganization(userIds);
            page.getRecords().forEach(user -> {
                UserTableResponse roleOrgModel = roleAndOrganizationMap.get(user.getId());
                if (Objects.nonNull(roleOrgModel)) {
                    user.setOrganizationList(roleOrgModel.getOrganizationList());
                    user.setUserRoleList(roleOrgModel.getUserRoleList());
                }
            });
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemUser addUser(AddUserRequest request) {
        checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        validateUserInfo(request.getEmail());
        SystemUser user = new SystemUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setEnable(true);
        user.setSource("LOCAL");
        user.setCreateUser(SessionUtils.getCurrentUserId());
        user.setUpdateUser(SessionUtils.getCurrentUserId());
        user.setPassword(passwordEncoder.encode("123456"));
        mapper.insert(user);
        if (!request.getUserRoleIdList().isEmpty()) {
            request.getUserRoleIdList().forEach(userRoleId -> {
                UserRoleRelation relation = new UserRoleRelation();
                relation.setRoleId(userRoleId);
                relation.setUserId(user.getId());
                relation.setSourceId(UserRoleScope.SYSTEM);
                relation.setOrganizationId(UserRoleScope.SYSTEM);
                relation.setCreateUser(SessionUtils.getCurrentUserId());
                userRoleRelationMapper.insert(relation);
            });
        }
        return user;
    }

    private void validateUserInfo(String email) {
        if (queryChain().where(SystemUser::getEmail).eq(email).exists()) {
            throw new CustomException("<邮箱重复>email");
        }
    }

    private void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem) {
        long count = QueryChain.of(SystemUserRole.class).where(SYSTEM_USER_ROLE.ID.in(roleIdList)
                        .and(SYSTEM_USER_ROLE.TYPE.eq("SYSTEM").when(isSystem))
                        .and(SYSTEM_USER_ROLE.SCOPE_ID.eq("GLOBAL")))
                .count();
        if (count != roleIdList.size()) {
            throw new CustomException("<角色不是全局系统角色>");
        }
    }

    private Map<String, UserTableResponse> selectGlobalUserRoleAndOrganization(@Valid @NotEmpty List<String> userIds) {
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).in(userIds).list();
        List<String> userRoleIdList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).distinct().toList();
        List<String> sourceIdList = userRoleRelations.stream().map(UserRoleRelation::getSourceId).distinct().toList();
        Map<String, SystemUserRole> userRoleMap = new HashMap<>();
        Map<String, SystemOrganization> organizationMap = new HashMap<>();
        if (!userRoleIdList.isEmpty()) {
            userRoleMap = QueryChain.of(SystemUserRole.class)
                    .where(SystemUserRole::getId).in(userRoleIdList)
                    .and(SystemUserRole::getScopeId).eq("global")
                    .list().stream()
                    .collect(Collectors.toMap(SystemUserRole::getId, item -> item));
        }
        if (!sourceIdList.isEmpty()) {
            organizationMap = QueryChain.of(SystemOrganization.class)
                    .where(SystemOrganization::getId).in(sourceIdList)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(SystemOrganization::getId, item -> item));
        }
        Map<String, UserTableResponse> returnMap = new HashMap<>();
        for (UserRoleRelation userRoleRelation : userRoleRelations) {
            UserTableResponse userInfo = returnMap.get(userRoleRelation.getUserId());
            if (userInfo == null) {
                userInfo = new UserTableResponse();
                userInfo.setId(userRoleRelation.getUserId());
                returnMap.put(userRoleRelation.getUserId(), userInfo);
            }
            SystemUserRole userRole = userRoleMap.get(userRoleRelation.getRoleId());
            if (userRole != null && StringUtils.equalsIgnoreCase(userRole.getType(), "system")) {
                userInfo.setUserRole(userRole);
            }
            SystemOrganization organization = organizationMap.get(userRoleRelation.getSourceId());
            if (organization != null && !userInfo.getOrganizationList().contains(organization)) {
                userInfo.setOrganization(organization);
            }
        }
        return returnMap;
    }
}
