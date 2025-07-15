package cn.master.luna.service.impl;

import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.entity.dto.OptionDTO;
import cn.master.luna.mapper.SystemOrganizationMapper;
import cn.master.luna.service.SystemOrganizationService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 组织 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
public class SystemOrganizationServiceImpl extends ServiceImpl<SystemOrganizationMapper, SystemOrganization>  implements SystemOrganizationService{

    @Override
    public List<OptionDTO> listAll() {
        return queryChain().list().stream().map(o -> new OptionDTO(o.getId(), o.getName())).toList();
    }
}
