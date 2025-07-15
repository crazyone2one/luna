package cn.master.luna.entity.request;

import cn.master.luna.constants.ScheduleTagType;
import cn.master.luna.entity.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 2025/7/9
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class SchedulePageRequest extends BasePageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String scheduleTagType = ScheduleTagType.LUNA.toString();
    private String orgId;
}
