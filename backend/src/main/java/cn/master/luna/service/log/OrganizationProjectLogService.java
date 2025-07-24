package cn.master.luna.service.log;

import cn.master.luna.constants.OperationLogConstants;
import cn.master.luna.constants.OperationLogModule;
import cn.master.luna.constants.OperationLogType;
import cn.master.luna.entity.SystemProject;
import cn.master.luna.entity.dto.LogDTO;
import cn.master.luna.mapper.SystemProjectMapper;
import cn.master.luna.util.JacksonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Created by 11's papa on 2025/7/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrganizationProjectLogService {
    @Resource
    private SystemProjectMapper projectMapper;

    public LogDTO addLog(SystemProject project) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.ORGANIZATION,
                project.getOrganizationId(),
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SETTING_ORGANIZATION_PROJECT,
                project.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(project));
        return dto;
    }

    public LogDTO updateLog(String id) {
        SystemProject project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.ORGANIZATION,
                    project.getOrganizationId(),
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_ORGANIZATION_PROJECT,
                    project.getName());

            dto.setOriginalValue(JacksonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }

    public LogDTO deleteLog(String id) {
        SystemProject project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.ORGANIZATION,
                    project.getOrganizationId(),
                    id,
                    null,
                    OperationLogType.DELETE.name(),
                    OperationLogModule.SETTING_ORGANIZATION_PROJECT,
                    project.getName());

            dto.setOriginalValue(JacksonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }
}
