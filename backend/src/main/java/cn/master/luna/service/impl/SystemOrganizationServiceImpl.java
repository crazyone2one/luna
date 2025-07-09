package cn.master.luna.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.mapper.SystemOrganizationMapper;
import cn.master.luna.service.SystemOrganizationService;
import org.springframework.stereotype.Service;

/**
 * 组织 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
public class SystemOrganizationServiceImpl extends ServiceImpl<SystemOrganizationMapper, SystemOrganization>  implements SystemOrganizationService{

}
