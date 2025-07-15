package cn.master.luna.service;

import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.entity.dto.OptionDTO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 组织 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
public interface SystemOrganizationService extends IService<SystemOrganization> {

    List<OptionDTO> listAll();
}
