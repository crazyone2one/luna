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
 * 用户组关系 实体类。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户组关系")
@Table("user_role_relation")
public class UserRoleRelation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户组关系ID
     */
    @Id
    @Schema(description = "用户组关系ID")
    private String id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 组ID
     */
    @Schema(description = "组ID")
    private String roleId;

    /**
     * 组织或项目ID
     */
    @Schema(description = "组织或项目ID")
    private String sourceId;

    /**
     * 记录所在的组织ID
     */
    @Schema(description = "记录所在的组织ID")
    private String organizationId;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

}
