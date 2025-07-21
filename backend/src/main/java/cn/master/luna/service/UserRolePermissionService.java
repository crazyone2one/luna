package cn.master.luna.service;

import cn.master.luna.entity.UserRolePermission;
import cn.master.luna.entity.request.PermissionSettingUpdateRequest;
import com.mybatisflex.core.service.IService;

import java.util.Set;

/**
 * 用户组权限 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
public interface UserRolePermissionService extends IService<UserRolePermission> {

    Set<String> getPermissionIdSetByRoleId(String id);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);
}
