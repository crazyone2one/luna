package cn.master.luna.entity.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Created by 11's papa on 2025/7/15
 */
@Data
public class ProjectSwitchRequest {
    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project.id.not_blank}")
    @Size(min = 1, max = 50, message = "{project.id.length_range}")
    private String projectId;
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user.id.not_blank}")
    private String userId;
}
