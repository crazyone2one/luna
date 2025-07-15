package cn.master.luna.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.luna.entity.OperationHistory;
import cn.master.luna.mapper.OperationHistoryMapper;
import cn.master.luna.service.OperationHistoryService;
import org.springframework.stereotype.Service;

/**
 * 变更记录 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-14
 */
@Service
public class OperationHistoryServiceImpl extends ServiceImpl<OperationHistoryMapper, OperationHistory>  implements OperationHistoryService{

}
