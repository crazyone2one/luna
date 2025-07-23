package cn.master.luna.service;

import cn.master.luna.entity.OperationLog;
import cn.master.luna.entity.dto.LogDTO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 操作日志 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-14
 */
public interface OperationLogService extends IService<OperationLog> {
    void batchAdd(List<LogDTO> logs);

    void add(LogDTO log);
}
