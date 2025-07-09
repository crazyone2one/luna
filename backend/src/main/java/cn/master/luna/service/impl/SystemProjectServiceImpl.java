package cn.master.luna.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.mapper.SystemProjectMapper;
import cn.master.luna.service.SystemProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Service
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject>  implements SystemProjectService{

}
