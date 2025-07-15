package cn.master.luna.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Created by 11's papa on 2025/7/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserRoleOptionDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private String id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "用户id")
    private String userId;
}
