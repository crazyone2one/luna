package cn.master.luna.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目 实体类。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "项目")
@Table("system_project")
public class SystemProject implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Id
    @Schema(description = "项目ID")
    private String id;

    /**
     * 项目编号
     */
    @Schema(description = "项目编号")
    private String num;

    /**
     * 组织ID
     */
    @Schema(description = "组织ID")
    private String organizationId;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String name;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述")
    private String description;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 删除时间
     */
    @Schema(description = "删除时间")
    private Long deleteTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 删除人
     */
    @Schema(description = "删除人")
    private String deleteUser;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

    /**
     * 模块设置
     */
    @Schema(description = "模块设置")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<String> moduleSetting;

    @Column(ignore = true,typeHandler = JacksonTypeHandler.class)
    private List<String> userIds;

}
