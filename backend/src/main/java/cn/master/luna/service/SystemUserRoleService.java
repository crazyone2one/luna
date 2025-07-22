package cn.master.luna.service;

import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.dto.PermissionDefinitionItem;
import cn.master.luna.entity.dto.UserExtendDTO;
import cn.master.luna.entity.dto.UserSelectOption;
import cn.master.luna.entity.request.OrganizationUserRoleMemberEditRequest;
import cn.master.luna.entity.request.OrganizationUserRoleMemberRequest;
import cn.master.luna.entity.request.PermissionSettingUpdateRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户组 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
public interface SystemUserRoleService extends IService<SystemUserRole> {
    List<UserSelectOption> getGlobalSystemRoleList();

    List<PermissionDefinitionItem> getPermissionSetting(String id);

    List<PermissionDefinitionItem> getOrgPermissionSetting(String id);

    List<PermissionDefinitionItem> getProjectPermissionSetting(String id);

    void updateProjectPermissionSetting(PermissionSettingUpdateRequest request);

    void updateOrgPermissionSetting(PermissionSettingUpdateRequest request);

    void updateGlobalPermissionSetting(PermissionSettingUpdateRequest request);

    Page<SystemUser> listMember(OrganizationUserRoleMemberRequest request);

    void removeMember(OrganizationUserRoleMemberEditRequest request);

    void addMember(OrganizationUserRoleMemberEditRequest request);

    List<SystemUserRole> getGlobalList();

    List<SystemUserRole> getOrgList(String organizationId);

    List<UserExtendDTO> getMember(String organizationId, String roleId, String keyword);
}
