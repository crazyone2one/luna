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
 * 用户组 实体类。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户组")
@Table("system_user_role")
public class SystemUserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 组ID
     */
    @Id
    @Schema(description = "组ID")
    private String id;

    /**
     * 组名称
     */
    @Schema(description = "组名称")
    private String name;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 是否是内置用户组
     */
    @Schema(description = "是否是内置用户组")
    private Boolean internal;

    /**
     * 所属类型 SYSTEM ORGANIZATION PROJECT
     */
    @Schema(description = "所属类型 SYSTEM ORGANIZATION PROJECT")
    private String type;

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
     * 创建人(操作人）
     */
    @Schema(description = "创建人(操作人）")
    private String createUser;

    /**
     * 应用范围
     */
    @Schema(description = "应用范围")
    private String scopeId;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

}
