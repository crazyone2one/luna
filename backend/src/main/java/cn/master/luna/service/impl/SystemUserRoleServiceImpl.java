package cn.master.luna.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.mapper.SystemUserRoleMapper;
import cn.master.luna.service.SystemUserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@Service
public class SystemUserRoleServiceImpl extends ServiceImpl<SystemUserRoleMapper, SystemUserRole>  implements SystemUserRoleService{

}
