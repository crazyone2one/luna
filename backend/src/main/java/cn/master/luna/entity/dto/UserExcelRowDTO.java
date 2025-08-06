package cn.master.luna.entity.dto;

import cn.master.luna.entity.UserTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/8/4
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class UserExcelRowDTO extends UserTemplate {
    private int dataIndex;
    private String errorMessage;
    private String userRoleId;
}
