package cn.master.luna.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 2025/7/9
 */
@Data
public class BaseCondition {
    @Schema(description =  "关键字")
    private String keyword;

    @Schema(description =  "过滤字段")
    private Map<String, List<String>> filter;
}
