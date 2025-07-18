package cn.master.luna.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/7/9
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class BasePageRequest extends BaseCondition {
    @Min(value = 1, message = "当前页码必须大于0")
    @Schema(description =  "当前页码")
    private int page;

    @Min(value = 5, message = "每页显示条数必须不小于5")
    @Max(value = 500, message = "每页显示条数不能大于500")
    @Schema(description =  "每页显示条数")
    private int pageSize;
}
