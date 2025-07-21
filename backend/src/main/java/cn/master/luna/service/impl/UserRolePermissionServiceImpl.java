package cn.master.luna.service.impl;

import cn.master.luna.entity.UserRolePermission;
import cn.master.luna.entity.request.PermissionSettingUpdateRequest;
import cn.master.luna.mapper.UserRolePermissionMapper;
import cn.master.luna.service.UserRolePermissionService;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission> implements UserRolePermissionService {

    @Override
    public Set<String> getPermissionIdSetByRoleId(String id) {
        return queryChain().where(UserRolePermission::getRoleId).eq(id).list()
                .stream()
                .map(UserRolePermission::getPermissionId).collect(Collectors.toSet());
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        List<PermissionSettingUpdateRequest.PermissionUpdateRequest> permissions = request.getPermissions();
        QueryWrapper queryWrapper = new QueryWrapper().where(UserRolePermission::getRoleId).eq(request.getUserRoleId())
                .and(UserRolePermission::getPermissionId).ne("PROJECT_BASE_INFO:READ");
        LogicDeleteManager.execWithoutLogicDelete(() -> mapper.deleteByQuery(queryWrapper));
        String groupId = request.getUserRoleId();
        permissions.forEach(permission -> {
            if (BooleanUtils.isTrue(permission.getEnable())) {
                String permissionId = permission.getId();
                UserRolePermission groupPermission = new UserRolePermission();
                groupPermission.setRoleId(groupId);
                groupPermission.setPermissionId(permissionId);
                mapper.insert(groupPermission);
            }
        });
    }
}
