package cn.master.luna.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户 实体类。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户")
@Table("system_user")
public class SystemUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Id
    @Schema(description = "用户ID")
    private String id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String name;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码")
    private String password;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 语言
     */
    @Schema(description = "语言")
    private String language;

    /**
     * 当前组织ID
     */
    @Schema(description = "当前组织ID")
    private String lastOrganizationId;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 来源：LOCAL OIDC CAS OAUTH2
     */
    @Schema(description = "来源：LOCAL OIDC CAS OAUTH2")
    private String source;

    /**
     * 当前项目ID
     */
    @Schema(description = "当前项目ID")
    private String lastProjectId;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

}
