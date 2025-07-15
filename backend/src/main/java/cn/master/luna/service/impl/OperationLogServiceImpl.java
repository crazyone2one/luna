package cn.master.luna.service.impl;

import cn.master.luna.entity.OperationHistory;
import cn.master.luna.entity.OperationLog;
import cn.master.luna.entity.dto.LogDTO;
import cn.master.luna.mapper.OperationHistoryMapper;
import cn.master.luna.mapper.OperationLogMapper;
import cn.master.luna.service.OperationLogService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-14
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    private final OperationHistoryMapper operationHistoryMapper;

    @Override
    public void batchAdd(List<LogDTO> logs) {
        if (logs.isEmpty()) {
            return;
        }
        logs.forEach(log -> {
            log.setContent(subStrContent(log.getContent()));
            mapper.insert(log);
            if (log.getHistory()) {
                operationHistoryMapper.insert(getHistory(log));
            }
        });
    }

    private String subStrContent(String content) {
        if (StringUtils.isNotBlank(content) && content.length() > 500) {
            return content.substring(0, 499);
        }
        return content;
    }

    private OperationHistory getHistory(LogDTO log) {
        OperationHistory history = new OperationHistory();
        BeanUtils.copyProperties(log, history);
        return history;
    }
}
