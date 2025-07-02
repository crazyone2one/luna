package cn.master.luna.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.luna.entity.UserRoleRelation;
import cn.master.luna.mapper.UserRoleRelationMapper;
import cn.master.luna.service.UserRoleRelationService;
import org.springframework.stereotype.Service;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@Service
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation>  implements UserRoleRelationService{

}
