package cn.master.luna.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.luna.entity.UserRolePermission;
import cn.master.luna.mapper.UserRolePermissionMapper;
import cn.master.luna.service.UserRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
public class UserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission>  implements UserRolePermissionService{

}
