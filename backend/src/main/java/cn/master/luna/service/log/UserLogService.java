package cn.master.luna.service.log;

import cn.master.luna.constants.HttpMethodConstants;
import cn.master.luna.constants.OperationLogConstants;
import cn.master.luna.constants.OperationLogModule;
import cn.master.luna.constants.OperationLogType;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.dto.LogDTO;
import cn.master.luna.entity.dto.LogDTOBuilder;
import cn.master.luna.entity.dto.TableBatchProcessDTO;
import cn.master.luna.service.SystemUserService;
import cn.master.luna.util.JacksonUtils;
import cn.master.luna.util.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/30
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserLogService {
    private final SystemUserService systemUserService;

    public List<LogDTO> resetPasswordLog(TableBatchProcessDTO request) {
        request.setSelectIds(systemUserService.getBatchUserIds(request));
        List<LogDTO> returnList = new ArrayList<>();
        List<SystemUser> userList = systemUserService.listByIds(request.getSelectIds());
        userList.forEach(user -> {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.UPDATE.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path("/system/user/reset/password")
                    .sourceId(user.getId())
                    .content(Translator.get("user.reset.password") + " : " + user.getName())
                    .originalValue(JacksonUtils.toJSONBytes(user))
                    .build().getLogDTO();
            returnList.add(dto);
        });
        return returnList;
    }
    public List<LogDTO> deleteLog(TableBatchProcessDTO request) {
        List<LogDTO> logDTOList = new ArrayList<>();
        request.getSelectIds().forEach(item -> {
            SystemUser user = systemUserService.getById(item);
            if (user != null) {
                LogDTO dto = LogDTOBuilder.builder()
                        .projectId(OperationLogConstants.SYSTEM)
                        .organizationId(OperationLogConstants.SYSTEM)
                        .type(OperationLogType.DELETE.name())
                        .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                        .method(HttpMethodConstants.POST.name())
                        .path("/system/user/delete")
                        .sourceId(user.getId())
                        .content(user.getName())
                        .originalValue(JacksonUtils.toJSONBytes(user))
                        .build().getLogDTO();
                logDTOList.add(dto);
            }
        });
        return logDTOList;
    }
}
