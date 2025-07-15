package cn.master.luna.entity;

import cn.master.luna.constants.Created;
import cn.master.luna.constants.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志 实体类。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "操作日志")
@Table("operation_log")
public class OperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.id.not_blank}", groups = {Updated.class})
    private String id;

    @Schema(description =  "项目id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{operation_log.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    @Schema(description =  "组织id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.organization_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{operation_log.organization_id.length_range}", groups = {Created.class, Updated.class})
    private String organizationId;

    /**
     * 操作时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "操作时间")
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    @Schema(description = "操作人")
    private String createUser;

    /**
     * 资源id
     */
    @Schema(description = "资源id")
    private String sourceId;

    @Schema(description = "操作方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.method.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{operation_log.method.length_range}", groups = {Created.class, Updated.class})
    private String method;

    @Schema(description =  "操作类型/add/update/delete", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{operation_log.type.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 20, message = "{operation_log.type.length_range}", groups = {Created.class, Updated.class})
    private String type;

    /**
     * 操作模块/api/case/scenario/ui
     */
    @Schema(description = "操作模块/api/case/scenario/ui")
    private String module;

    /**
     * 操作详情
     */
    @Schema(description = "操作详情")
    private String content;

    /**
     * 操作路径
     */
    @Schema(description = "操作路径")
    private String path;

    /**
     * 变更前内容
     */
    @Schema(description = "变更前内容")
    private byte[] originalValue;

    /**
     * 变更后内容
     */
    @Schema(description = "变更后内容")
    private byte[] modifiedValue;

}
