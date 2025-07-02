package cn.master.luna.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.mapper.SystemUserMapper;
import cn.master.luna.service.SystemUserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-01
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>  implements SystemUserService{

}
