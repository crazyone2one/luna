package cn.master.luna.entity;

import cn.master.luna.constants.Created;
import cn.master.luna.constants.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    @Schema(description =  "组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user_role.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{user_role.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    /**
     * 组名称
     */
    @Schema(description = "组名称")
    @NotBlank(message = "{user_role.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{user_role.name.length_range}", groups = {Created.class, Updated.class})
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
    @NotNull(message = "{user_role.internal.not_blank}", groups = {Created.class})
    private Boolean internal;

    /**
     * 所属类型 SYSTEM ORGANIZATION PROJECT
     */
    @Schema(description =  "所属类型 SYSTEM ORGANIZATION PROJECT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user_role.type.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 20, message = "{user_role.type.length_range}", groups = {Created.class, Updated.class})
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
    @Schema(description =  "应用范围", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user_role.scope_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{user_role.scope_id.length_range}", groups = {Created.class, Updated.class})
    private String scopeId;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

}
