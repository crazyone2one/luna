package cn.master.luna.entity.dto;

import cn.master.luna.entity.UserTemplate;
import lombok.Data;

/**
 * @author Created by 11's papa on 2025/8/4
 */
@Data
public class UserExcelRowDTO extends UserTemplate {
    public int dataIndex;
    public String errorMessage;
    public String userRoleId;
}
