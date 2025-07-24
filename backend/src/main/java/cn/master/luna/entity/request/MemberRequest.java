package cn.master.luna.entity.request;

import cn.master.luna.entity.dto.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/7/24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberRequest extends BasePageRequest {

    @Schema(description =  "组织ID或项目ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private String sourceId;
}
