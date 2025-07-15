package cn.master.luna.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变更记录 实体类。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变更记录")
@Table("operation_history")
public class OperationHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Schema(description = "主键")
    private String id;

    /**
     * 项目id
     */
    @Schema(description = "项目id")
    private String projectId;

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

    /**
     * 操作类型/add/update/delete
     */
    @Schema(description = "操作类型/add/update/delete")
    private String type;

    /**
     * 操作模块/api/case/scenario/ui
     */
    @Schema(description = "操作模块/api/case/scenario/ui")
    private String module;

    /**
     * 关联id（关联变更记录id来源）
     */
    @Schema(description = "关联id（关联变更记录id来源）")
    private Long refId;

}
