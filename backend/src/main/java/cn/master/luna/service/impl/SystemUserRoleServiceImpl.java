package cn.master.luna.service.impl;

import cn.master.luna.config.PermissionCache;
import cn.master.luna.constants.InternalUserRole;
import cn.master.luna.constants.UserRoleScope;
import cn.master.luna.constants.UserRoleType;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.entity.dto.Permission;
import cn.master.luna.entity.dto.PermissionDefinitionItem;
import cn.master.luna.entity.dto.UserExtendDTO;
import cn.master.luna.entity.dto.UserSelectOption;
import cn.master.luna.entity.request.OrganizationUserRoleMemberEditRequest;
import cn.master.luna.entity.request.OrganizationUserRoleMemberRequest;
import cn.master.luna.entity.request.PermissionSettingUpdateRequest;
import cn.master.luna.exception.CustomException;
import cn.master.luna.mapper.SystemUserRoleMapper;
import cn.master.luna.mapper.UserRoleRelationMapper;
import cn.master.luna.service.SystemUserRoleService;
import cn.master.luna.service.UserRolePermissionService;
import cn.master.luna.util.JacksonUtils;
import cn.master.luna.util.ServiceUtils;
import cn.master.luna.util.SessionUtils;
import cn.master.luna.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.luna.constants.InternalUserRole.MEMBER;
import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.luna.exception.SystemResultCode.*;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@Service
@RequiredArgsConstructor
public class SystemUserRoleServiceImpl extends ServiceImpl<SystemUserRoleMapper, SystemUserRole> implements SystemUserRoleService {
    private final UserRolePermissionService userRolePermissionService;
    private final UserRoleRelationMapper userRoleRelationMapper;

    @Resource
    private PermissionCache permissionCache;

    @Override
    public List<UserSelectOption> getGlobalSystemRoleList() {
        List<UserSelectOption> returnList = new ArrayList<>();
        List<SystemUserRole> userRoles = queryChain()
                .where(SystemUserRole::getScopeId).eq("global").and(SystemUserRole::getType).eq(UserRoleType.SYSTEM.name())
                .list();
        for (SystemUserRole userRole : userRoles) {
            UserSelectOption userRoleOption = new UserSelectOption();
            userRoleOption.setId(userRole.getId());
            userRoleOption.setName(userRole.getName());
            userRoleOption.setSelected(Strings.CS.equals(userRole.getId(), MEMBER.getValue()));
            userRoleOption.setCloseable(!Strings.CS.equals(userRole.getId(), MEMBER.getValue()));
            returnList.add(userRoleOption);
        }
        return returnList;
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        SystemUserRole userRole = getWithCheck(id);
        checkGlobalUserRole(userRole);
        return getPermissionSettings(userRole);
    }

    @Override
    public List<PermissionDefinitionItem> getOrgPermissionSetting(String id) {
        SystemUserRole userRole = getWithCheck(id);
        checkOrgUserRole(userRole);
        return getPermissionSettings(userRole);
    }

    @Override
    public List<PermissionDefinitionItem> getProjectPermissionSetting(String id) {
        SystemUserRole userRole = getWithCheck(id);
        checkProjectUserRole(userRole);
        return getPermissionSettings(userRole);
    }

    @Override
    public void updateProjectPermissionSetting(PermissionSettingUpdateRequest request) {
        SystemUserRole userRole = mapper.selectOneById(request.getUserRoleId());
        if (userRole == null) {
            throw new CustomException(Translator.get("user_role_not_exist"));
        }
        checkProjectUserRole(userRole);
        checkGlobalUserRole(userRole);
        userRolePermissionService.updatePermissionSetting(request);
    }

    @Override
    public void updateOrgPermissionSetting(PermissionSettingUpdateRequest request) {
        SystemUserRole userRole = mapper.selectOneById(request.getUserRoleId());
        if (userRole == null) {
            throw new CustomException(Translator.get("user_role_not_exist"));
        }
        checkOrgUserRole(userRole);
        checkGlobalUserRole(userRole);
        userRolePermissionService.updatePermissionSetting(request);
    }

    @Override
    public void updateGlobalPermissionSetting(PermissionSettingUpdateRequest request) {
        SystemUserRole userRole = getWithCheck(request.getUserRoleId());
        checkGlobalUserRole(userRole);
        // 内置管理员级别用户组无法更改权限
        if (Strings.CS.equalsAny(userRole.getId(), InternalUserRole.ADMIN.getValue(),
                InternalUserRole.ORG_ADMIN.getValue(), InternalUserRole.PROJECT_ADMIN.getValue())) {
            throw new CustomException(ADMIN_USER_ROLE_PERMISSION);
        }
        userRolePermissionService.updatePermissionSetting(request);
    }

    @Override
    public Page<SystemUser> listMember(OrganizationUserRoleMemberRequest request) {
        return queryChain()
                .select(SYSTEM_USER.ALL_COLUMNS)
                .from(USER_ROLE_RELATION).leftJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where((USER_ROLE_RELATION.ROLE_ID.eq(request.getUserRoleId()))
                        .and(SYSTEM_USER.NAME.like(request.getKeyword())
                                .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                                .or(SYSTEM_USER.PHONE.like(request.getKeyword()))))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), SystemUser.class);
    }

    @Override
    public void removeMember(OrganizationUserRoleMemberEditRequest request) {
        String removeUserId = request.getUserIds().getFirst();
        checkMemberParam(removeUserId, request.getUserRoleId());
        //检查移除的是不是管理员
        if (Strings.CS.equals(request.getUserRoleId(), InternalUserRole.ORG_ADMIN.getValue())) {
            boolean exists = QueryChain.of(UserRoleRelation.class)
                    .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())
                            .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue())))
                    .exists();
            if (!exists) {
                throw new CustomException(Translator.get("keep_at_least_one_administrator"));
            }
        }
        // 移除组织-用户组的成员, 若成员只存在该组织下唯一用户组, 则提示不能移除
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class);
        boolean exists = userRoleRelationQueryChain.where(USER_ROLE_RELATION.ROLE_ID.ne(request.getUserRoleId())
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId()))).exists();
        if (!exists) {
            throw new CustomException(Translator.get("org_at_least_one_user_role_require"));
        }
        userRoleRelationQueryChain.clear();
        QueryChain<UserRoleRelation> chain = userRoleRelationQueryChain.where(USER_ROLE_RELATION.ROLE_ID.eq(request.getUserRoleId())
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId()))
                .and(USER_ROLE_RELATION.USER_ID.eq(removeUserId)));
        userRoleRelationMapper.deleteByQuery(chain);
    }

    @Override
    public void addMember(OrganizationUserRoleMemberEditRequest request) {
        request.getUserIds().forEach(userId -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            userRoleRelation.setUserId(userId);
            userRoleRelation.setRoleId(request.getUserRoleId());
            userRoleRelation.setSourceId(request.getOrganizationId());
            userRoleRelation.setCreateUser(SessionUtils.getUserName());
            userRoleRelation.setOrganizationId(request.getOrganizationId());
            userRoleRelationMapper.insert(userRoleRelation);
        });
    }

    @Override
    public List<SystemUserRole> getGlobalList() {
        List<SystemUserRole> userRoles = queryChain().where(SYSTEM_USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL)).list();
        // 先按照类型排序，再按照创建时间排序
        userRoles.sort(Comparator.comparingInt(this::getTypeOrder)
                .thenComparingInt(item -> getInternal(item.getInternal()))
                .thenComparing(SystemUserRole::getCreateTime));
        return userRoles;
    }

    @Override
    public List<SystemUserRole> getOrgList(String organizationId) {
        List<SystemUserRole> userRoles = queryChain().where(SYSTEM_USER_ROLE.TYPE.eq(UserRoleType.ORGANIZATION.name())
                .and(SYSTEM_USER_ROLE.SCOPE_ID.in(Arrays.asList(organizationId, "global")))).list();
        userRoles.sort(Comparator.comparing(SystemUserRole::getInternal).thenComparing(SystemUserRole::getScopeId)
                .thenComparing(Comparator.comparing(SystemUserRole::getCreateTime).thenComparing(SystemUserRole::getId).reversed())
                .reversed());
        return userRoles;
    }

    @Override
    public List<UserExtendDTO> getMember(String organizationId, String roleId, String keyword) {
        List<UserExtendDTO> userExtendDTOS = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)).list();
        if (!userRoleRelations.isEmpty()) {
            Map<String, List<String>> userRoleMap = userRoleRelations.stream().collect(Collectors.groupingBy(UserRoleRelation::getUserId,
                    Collectors.mapping(UserRoleRelation::getRoleId, Collectors.toList())));
            userRoleMap.forEach((k, v) -> {
                UserExtendDTO userExtendDTO = new UserExtendDTO();
                userExtendDTO.setId(k);
                v.forEach(roleItem -> {
                    if (Strings.CS.equals(roleItem, roleId)) {
                        // 该用户已存在用户组关系, 设置为选中状态
                        userExtendDTO.setCheckRoleFlag(true);
                    }
                });
                userExtendDTOS.add(userExtendDTO);
            });
            // 设置用户信息, 用户不存在或者已删除, 则不展示
            List<String> userIds = userExtendDTOS.stream().map(UserExtendDTO::getId).toList();
            List<SystemUser> users = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.in(userIds)
                    .and(SYSTEM_USER.NAME.like(keyword).or(SYSTEM_USER.EMAIL.like(keyword)))).list();
            if (!users.isEmpty()) {
                Map<String, SystemUser> userMap = users.stream().collect(Collectors.toMap(SystemUser::getId, user -> user));
                userExtendDTOS.removeIf(userExtend -> {
                    if (userMap.containsKey(userExtend.getId())) {
                        BeanUtils.copyProperties(userMap.get(userExtend.getId()), userExtend);
                        return false;
                    }
                    return true;
                });
            } else {
                userExtendDTOS.clear();
            }
        }
        userExtendDTOS.sort(Comparator.comparing(UserExtendDTO::getName));
        return userExtendDTOS;
    }

    private int getInternal(Boolean internal) {
        return BooleanUtils.isTrue(internal) ? 0 : 1;
    }

    private int getTypeOrder(SystemUserRole userRole) {
        Map<String, Integer> typeOrderMap = new HashMap<>(3);
        typeOrderMap.put(UserRoleType.SYSTEM.name(), 1);
        typeOrderMap.put(UserRoleType.ORGANIZATION.name(), 2);
        typeOrderMap.put(UserRoleType.PROJECT.name(), 3);
        return typeOrderMap.getOrDefault(userRole.getType(), 0);
    }

    private void checkMemberParam(String userId, String roleId) {
        boolean exists = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.eq(userId)).exists();
        if (!exists) {
            throw new CustomException(Translator.get("user_not_exist"));
        }
        if (mapper.selectOneById(roleId) == null) {
            throw new CustomException(Translator.get("user_role_not_exist"));
        }
    }

    private void checkProjectUserRole(SystemUserRole userRole) {
        if (!UserRoleType.PROJECT.name().equals(userRole.getType())) {
            throw new CustomException(NO_PROJECT_USER_ROLE_PERMISSION);
        }
    }

    private void checkOrgUserRole(SystemUserRole userRole) {
        if (!UserRoleType.ORGANIZATION.name().equals(userRole.getType())) {
            throw new CustomException(NO_ORG_USER_ROLE_PERMISSION);
        }
    }

    private List<PermissionDefinitionItem> getPermissionSettings(SystemUserRole userRole) {
        // 获取该用户组拥有的权限
        Set<String> permissionIds = userRolePermissionService.getPermissionIdSetByRoleId(userRole.getId());
        // 获取所有的权限
        List<PermissionDefinitionItem> permissionDefinition = permissionCache.getPermissionDefinition();
        permissionDefinition = JacksonUtils.parseArray(JacksonUtils.toJSONString(permissionDefinition), PermissionDefinitionItem.class);
        // 过滤该用户组级别的菜单，例如系统级别 (管理员返回所有权限位)
        permissionDefinition = permissionDefinition.stream()
                .filter(item -> Strings.CS.equals(item.getType(), userRole.getType()) || Strings.CS.equals(userRole.getId(), InternalUserRole.ADMIN.getValue()))
                .sorted(Comparator.comparing(PermissionDefinitionItem::getOrder))
                .toList();
        // 设置勾选项
        for (PermissionDefinitionItem firstLevel : permissionDefinition) {
            List<PermissionDefinitionItem> children = firstLevel.getChildren();
            boolean allCheck = true;
            firstLevel.setName(Translator.get(firstLevel.getName()));
            for (PermissionDefinitionItem secondLevel : children) {
                List<Permission> permissions = secondLevel.getPermissions();
                secondLevel.setName(Translator.get(secondLevel.getName()));
                if (CollectionUtils.isEmpty(permissions)) {
                    continue;
                }
                boolean secondAllCheck = true;
                for (Permission p : permissions) {
                    if (StringUtils.isNotBlank(p.getName())) {
                        // 有 name 字段翻译 name 字段
                        p.setName(Translator.get(p.getName()));
                    } else {
                        p.setName(translateDefaultPermissionName(p));
                    }
                    // 管理员默认勾选全部二级权限位
                    if (permissionIds.contains(p.getId()) || Strings.CS.equals(userRole.getId(), InternalUserRole.ADMIN.getValue())) {
                        p.setEnable(true);
                    } else {
                        // 如果权限有未勾选，则二级菜单设置为未勾选
                        p.setEnable(false);
                        secondAllCheck = false;
                    }
                }
                secondLevel.setEnable(secondAllCheck);
                if (!secondAllCheck) {
                    // 如果二级菜单有未勾选，则一级菜单设置为未勾选
                    allCheck = false;
                }
            }
            firstLevel.setEnable(allCheck);
        }
        return permissionDefinition;
    }

    private String translateDefaultPermissionName(Permission p) {
        String[] idSplit = p.getId().split(":");
        String permissionKey = idSplit[idSplit.length - 1];
        Map<String, String> translationMap = new HashMap<>();
        translationMap.put("READ", "permission.read");
        translationMap.put("READ+ADD", "permission.add");
        translationMap.put("READ+UPDATE", "permission.edit");
        translationMap.put("READ+DELETE", "permission.delete");
        translationMap.put("READ+IMPORT", "permission.import");
        translationMap.put("READ+RECOVER", "permission.recover");
        translationMap.put("READ+EXPORT", "permission.export");
        translationMap.put("READ+EXECUTE", "permission.execute");
        translationMap.put("READ+DEBUG", "permission.debug");
        return Translator.get(translationMap.get(permissionKey));
    }

    private void checkGlobalUserRole(SystemUserRole userRole) {
        if (!Strings.CS.equals(userRole.getScopeId(), UserRoleScope.GLOBAL)) {
            throw new CustomException(GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    private SystemUserRole getWithCheck(String id) {
        return checkResourceExist(mapper.selectOneById(id));
    }

    private SystemUserRole checkResourceExist(SystemUserRole userRole) {
        return ServiceUtils.checkResourceExist(userRole, "permission.system_user_role.name");
    }
}
