package cn.master.luna.handler.listener;

import cn.master.luna.entity.UserTemplate;
import cn.master.luna.entity.dto.ExcelParseDTO;
import cn.master.luna.entity.dto.UserExcelRowDTO;
import cn.master.luna.handler.excel.UserExcelValidateHelper;
import cn.master.luna.util.Translator;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;

/**
 * @author Created by 11's papa on 2025/8/4
 */
@Getter
@Slf4j
public class UserImportEventListener extends AnalysisEventListener<UserTemplate> {
    private final ExcelParseDTO<UserExcelRowDTO> excelParseDTO;

    public UserImportEventListener() {
        excelParseDTO = new ExcelParseDTO<>();
    }

    @Override
    public void invoke(UserTemplate userTemplate, AnalysisContext analysisContext) {
        String errMsg;
        Integer rowIndex = analysisContext.readRowHolder().getRowIndex();
        try {
            errMsg = UserExcelValidateHelper.validateEntity(userTemplate);
            if (StringUtils.isEmpty(errMsg)) {
                errMsg = businessValidate(userTemplate);
            }
        } catch (Exception e) {
            errMsg = Translator.get("excel.parse.error");
            log.error(e.getMessage(), e);
        }
        UserExcelRowDTO userExcelRowDTO = new UserExcelRowDTO();
        BeanUtils.copyProperties(userTemplate, userExcelRowDTO);
        userExcelRowDTO.setDataIndex(rowIndex);
        if (StringUtils.isEmpty(errMsg)) {
            excelParseDTO.addRowData(userExcelRowDTO);
        } else {
            userExcelRowDTO.setErrorMessage(errMsg);
            excelParseDTO.addErrorRowData(rowIndex, userExcelRowDTO);
        }
    }

    private String businessValidate(UserTemplate userTemplate) {
        if (CollectionUtils.isNotEmpty(excelParseDTO.getDataList())) {
            for (UserExcelRowDTO data : excelParseDTO.getDataList()) {
                if (Strings.CS.equals(data.getEmail(), userTemplate.getEmail())) {
                    return Translator.get("user.email.repeat") + ":" + userTemplate.getEmail();
                }
            }
        }
        return null;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.debug("所有数据解析完成！");
    }
}
