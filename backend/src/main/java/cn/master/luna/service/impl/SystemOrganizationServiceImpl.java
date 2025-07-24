package cn.master.luna.service.impl;

import cn.master.luna.constants.*;
import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.entity.dto.LogDTO;
import cn.master.luna.entity.dto.OptionDTO;
import cn.master.luna.entity.request.OrganizationMemberRequest;
import cn.master.luna.exception.CustomException;
import cn.master.luna.mapper.SystemOrganizationMapper;
import cn.master.luna.mapper.UserRoleRelationMapper;
import cn.master.luna.service.OperationLogService;
import cn.master.luna.service.SystemOrganizationService;
import cn.master.luna.util.JacksonUtils;
import cn.master.luna.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * 组织 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
@RequiredArgsConstructor
public class SystemOrganizationServiceImpl extends ServiceImpl<SystemOrganizationMapper, SystemOrganization> implements SystemOrganizationService {
    private final OperationLogService operationLogService;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private static final String ADD_MEMBER_PATH = "/organization/system/add-member";
    private static final String REMOVE_MEMBER_PATH = "/organization/system/remove-member";
    public static final Integer DEFAULT_REMAIN_DAY_COUNT = 30;
    private static final Long DEFAULT_ORGANIZATION_NUM = 100001L;

    @Override
    public List<OptionDTO> listAll() {
        return queryChain().list().stream().map(o -> new OptionDTO(o.getId(), o.getName())).toList();
    }

    @Override
    public List<OptionDTO> getUserRoleList(String organizationId) {
        checkOrgExistById(organizationId);
        List<String> scopeIds = Arrays.asList("global", organizationId);
        List<OptionDTO> userRoleList = new ArrayList<>();
        List<SystemUserRole> userRoles = QueryChain.of(SystemUserRole.class).where(SYSTEM_USER_ROLE.TYPE.eq(UserRoleType.ORGANIZATION.toString())
                .and(SYSTEM_USER_ROLE.SCOPE_ID.in(scopeIds))).list();
        setUserRoleList(userRoleList, userRoles);
        return userRoleList;
    }

    @Override
    public void addMemberBySystem(OrganizationMemberRequest request, String createUser) {
        List<LogDTO> logs = new ArrayList<>();
        addMemberAndGroup(request, createUser);
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SystemUser::getId).in(request.getUserIds()).list();
        List<String> nameList = users.stream().map(SystemUser::getName).toList();
        setLog(request.getOrganizationId(), createUser, OperationLogType.ADD.name(), Translator.get("add") + Translator.get("organization_member_log") + ": " + StringUtils.join(nameList, ","), ADD_MEMBER_PATH, null, null, logs);
        operationLogService.batchAdd(logs);
    }

    private void setLog(String organizationId, String createUser, String type, String content, String path, Object originalValue, Object modifiedValue, List<LogDTO> logs) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                organizationId,
                createUser,
                type,
                OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                content);
        dto.setPath(path);
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setOriginalValue(JacksonUtils.toJSONBytes(originalValue));
        dto.setModifiedValue(JacksonUtils.toJSONBytes(modifiedValue));
        logs.add(dto);
    }

    private void addMemberAndGroup(OrganizationMemberRequest request, String createUser) {
        checkOrgExistByIds(List.of(request.getOrganizationId()));
        Map<String, SystemUser> userMap = checkUserExist(request.getUserIds());
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        for (String userId : request.getUserIds()) {
            if (userMap.get(userId) == null) {
                throw new CustomException(Translator.get("user.not.exist") + ", id: " + userId);
            }
            long count = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())
                    .and(USER_ROLE_RELATION.USER_ID.eq(userId))).count();
            if (count > 0) {
                continue;
            }
            request.getUserRoleIds().forEach(userRoleId -> {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(userId);
                userRoleRelation.setSourceId(request.getOrganizationId());
                userRoleRelation.setRoleId(userRoleId);
                userRoleRelation.setCreateUser(createUser);
                userRoleRelation.setOrganizationId(request.getOrganizationId());
                userRoleRelations.add(userRoleRelation);
            });
        }
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
    }

    private Map<String, SystemUser> checkUserExist(@NotEmpty(message = "{user.id.not_blank}") List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new CustomException(Translator.get("user.not.empty"));
        }
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SystemUser::getId).in(userIds).list();
        if (CollectionUtils.isEmpty(users)) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
        return users.stream().collect(Collectors.toMap(SystemUser::getId, user -> user));
    }

    private void checkOrgExistByIds(List<String> organizationIds) {
        long count = queryChain().where(SystemOrganization::getId).in(organizationIds).count();
        if (count < organizationIds.size()) {
            throw new CustomException(Translator.get("organization_not_exist"));
        }
    }

    private void setUserRoleList(List<OptionDTO> userRoleList, List<SystemUserRole> userRoles) {
        for (SystemUserRole userRole : userRoles) {
            OptionDTO optionDTO = new OptionDTO();
            optionDTO.setId(userRole.getId());
            optionDTO.setName(userRole.getName());
            userRoleList.add(optionDTO);
        }
    }

    private void checkOrgExistById(String organizationId) {
        SystemOrganization organization = mapper.selectOneById(organizationId);
        if (organization == null) {
            throw new CustomException(Translator.get("organization_not_exist"));
        }
    }
}
