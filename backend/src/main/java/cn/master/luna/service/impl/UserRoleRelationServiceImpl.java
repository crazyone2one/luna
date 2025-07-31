package cn.master.luna.service.impl;

import cn.master.luna.constants.*;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.entity.dto.LogDTO;
import cn.master.luna.entity.dto.UserRoleRelationUserDTO;
import cn.master.luna.entity.request.GlobalUserRoleRelationQueryRequest;
import cn.master.luna.entity.request.GlobalUserRoleRelationUpdateRequest;
import cn.master.luna.exception.CustomException;
import cn.master.luna.mapper.SystemUserRoleMapper;
import cn.master.luna.mapper.UserRoleRelationMapper;
import cn.master.luna.service.OperationLogService;
import cn.master.luna.service.UserRoleRelationService;
import cn.master.luna.util.JacksonUtils;
import cn.master.luna.util.ServiceUtils;
import cn.master.luna.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.luna.exception.SystemResultCode.*;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@Service
@RequiredArgsConstructor
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements UserRoleRelationService {
    private final SystemUserRoleMapper systemUserRoleMapper;
    private final OperationLogService operationLogService;

    @Override
    public Page<UserRoleRelationUserDTO> listUser(GlobalUserRoleRelationQueryRequest request) {
        Page<UserRoleRelationUserDTO> page = queryChain()
                .select(USER_ROLE_RELATION.ID, SYSTEM_USER.ID.as("userId"), SYSTEM_USER.NAME, SYSTEM_USER.EMAIL, SYSTEM_USER.PHONE)
                .from(USER_ROLE_RELATION).innerJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(request.getRoleId()))
                        .and(SYSTEM_USER.NAME.like(request.getKeyword())
                                .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                                .or(SYSTEM_USER.PHONE.like(request.getKeyword()))))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserRoleRelationUserDTO.class);
        SystemUserRole userRole = QueryChain.of(SystemUserRole.class).where(SystemUserRole::getId).eq(request.getRoleId()).one();
        checkSystemUserGroup(userRole);
        checkGlobalUserRole(userRole);
        return page;
    }

    @Override
    public void delete(String id) {
        UserRoleRelation userRoleRelation = mapper.selectOneById(id);
        SystemUserRole userRole = userRoleRelation == null ? null : systemUserRoleMapper.selectOneById(userRoleRelation.getRoleId());
        ServiceUtils.checkResourceExist(userRole, "permission.system_user_role.name");
        assert userRole != null;
        checkSystemUserGroup(userRole);
        checkGlobalUserRole(userRole);
        mapper.deleteById(id);
        boolean exists = queryChain().where(USER_ROLE_RELATION.USER_ID.eq(userRoleRelation.getUserId())
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(UserRoleScope.SYSTEM))).exists();
        if (!exists) {
            throw new CustomException(GLOBAL_USER_ROLE_LIMIT);
        }
    }

    @Override
    public void add(GlobalUserRoleRelationUpdateRequest request) {
        checkGlobalSystemUserRoleLegality(Collections.singletonList(request.getRoleId()));
        checkUserLegality(request.getUserIds());
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getUserIds().forEach(userId -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
            userRoleRelation.setUserId(userId);
            checkExist(userRoleRelation);
            userRoleRelation.setOrganizationId(UserRoleScope.SYSTEM);
            userRoleRelation.setCreateUser(request.getCreateUser());
            userRoleRelations.add(userRoleRelation);
        });
        mapper.insertBatch(userRoleRelations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserSystemGlobalRole(SystemUser user, String operator, List<String> roleList) {
        List<String> deleteRoleList = new ArrayList<>();
        List<UserRoleRelation> saveList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelationList = selectGlobalRoleByUserId(user.getId());
        List<String> userSavedRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).toList();
        //获取要移除的权限
        for (String userSavedRoleId : userSavedRoleIdList) {
            if (!roleList.contains(userSavedRoleId)) {
                deleteRoleList.add(userSavedRoleId);
            }
        }
        //获取要添加的权限
        for (String roleId : roleList) {
            if (!userSavedRoleIdList.contains(roleId)) {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(user.getId());
                userRoleRelation.setRoleId(roleId);
                userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
                userRoleRelation.setCreateUser(operator);
                userRoleRelation.setOrganizationId(UserRoleScope.SYSTEM);
                saveList.add(userRoleRelation);
            }
        }
        if (CollectionUtils.isNotEmpty(deleteRoleList)) {
            List<String> deleteIdList = new ArrayList<>();
            userRoleRelationList.forEach(item -> {
                if (deleteRoleList.contains(item.getRoleId())) {
                    deleteIdList.add(item.getId());
                }
            });
            mapper.deleteBatchByIds(deleteIdList);
            //记录删除日志
            operationLogService.batchAdd(getBatchLogs(deleteRoleList, user, "updateUser", operator, OperationLogType.DELETE.name()));
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            //系统级权限不会太多，所以暂时不分批处理
            saveList.forEach(item -> mapper.insert(item));
            //记录添加日志
            operationLogService.batchAdd(getBatchLogs(saveList.stream().map(UserRoleRelation::getRoleId).toList(),
                    user, "updateUser", operator, OperationLogType.ADD.name()));
        }
    }

    private List<LogDTO> getBatchLogs(@Valid @NotEmpty List<String> userRoleId,
                                      @Valid SystemUser user,
                                      @Valid @NotEmpty String operationMethod,
                                      @Valid @NotEmpty String operator,
                                      @Valid @NotEmpty String operationType) {
        List<LogDTO> logs = new ArrayList<>();
        List<SystemUserRole> userRoleList = systemUserRoleMapper.selectListByIds(userRoleId);
        userRoleList.forEach(userRole -> {
            LogDTO log = new LogDTO();
            log.setProjectId(OperationLogConstants.SYSTEM);
            log.setOrganizationId(OperationLogConstants.SYSTEM);
            log.setType(operationType);
            log.setCreateUser(operator);
            log.setModule(OperationLogModule.SETTING_SYSTEM_USER_SINGLE);
            log.setMethod(operationMethod);
            log.setSourceId(user.getId());
            log.setContent(user.getName() + StringUtils.SPACE
                    + Translator.get(StringUtils.lowerCase(operationType)) + StringUtils.SPACE
                    + Translator.get("permission.project_group.name") + StringUtils.SPACE
                    + userRole.getName());
            log.setOriginalValue(JacksonUtils.toJSONBytes(userRole));
            logs.add(log);
        });
        return logs;
    }

    private List<UserRoleRelation> selectGlobalRoleByUserId(String userId) {
        List<String> userRoleIds = QueryChain.of(SystemUserRole.class)
                .select(SystemUserRole::getId)
                .from(SYSTEM_USER_ROLE)
                .where(SYSTEM_USER_ROLE.TYPE.eq("SYSTEM").and(SYSTEM_USER_ROLE.SCOPE_ID.eq("global")))
                .listAs(String.class);
        return queryChain()
                .where(USER_ROLE_RELATION.USER_ID.eq(userId))
                .and(USER_ROLE_RELATION.ROLE_ID.in(userRoleIds))
                .list();
    }

    private void checkExist(UserRoleRelation userRoleRelation) {
        boolean exists = queryChain().where(USER_ROLE_RELATION.USER_ID.eq(userRoleRelation.getUserId())
                .and(USER_ROLE_RELATION.ROLE_ID.eq(userRoleRelation.getRoleId()))).exists();
        if (exists) {
            throw new CustomException(USER_ROLE_RELATION_EXIST);
        }
    }

    private void checkUserLegality(List<String> userIds) {
        if (userIds.isEmpty()) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
        long count = QueryChain.of(SystemUser.class).where(SystemUser::getId).in(userIds).count();
        if (count != userIds.size()) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
    }

    private void checkGlobalSystemUserRoleLegality(List<String> checkIdList) {
        List<SystemUserRole> systemUserRoles = systemUserRoleMapper.selectListByIds(checkIdList);
        if (systemUserRoles.size() != checkIdList.size()) {
            throw new CustomException(Translator.get("user_role_not_exist"));
        }
        systemUserRoles.forEach(systemUserRole -> {
            checkSystemUserGroup(systemUserRole);
            checkGlobalUserRole(systemUserRole);
        });
    }

    private void checkSystemUserGroup(SystemUserRole userRole) {
        if (!Strings.CS.equals(userRole.getType(), UserRoleType.SYSTEM.name())) {
            throw new CustomException(GLOBAL_USER_ROLE_RELATION_SYSTEM_PERMISSION);
        }
    }

    private void checkGlobalUserRole(SystemUserRole userRole) {
        if (!Strings.CS.equals(userRole.getScopeId(), UserRoleScope.GLOBAL)) {
            throw new CustomException(GLOBAL_USER_ROLE_PERMISSION);
        }
    }
}
