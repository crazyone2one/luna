package cn.master.luna.entity.request;

import cn.master.luna.constants.Created;
import cn.master.luna.constants.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/14
 */
@Data
public class AddUserRequest {
    @Schema(description =  "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名称不能为空", groups = {Created.class})
    @Size(min = 1, max = 255, message = "用户名称长度必须在 {min} 和 {max} 之间", groups = {Created.class, Updated.class})
    private String name;

    @Schema(description =  "用户邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{user.email.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 64, message = "{user.email.length_range}", groups = {Created.class, Updated.class})
    @Email(message = "{user.email.invalid}", groups = {Created.class, Updated.class})
    private String email;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description =  "用户ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String id;
    @Schema(description =  "用户组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(groups = {Created.class, Updated.class}, message = "{user_role.id.not_blank}")
    List<@Valid @NotBlank(message = "{user_role.id.not_blank}", groups = {Created.class, Updated.class}) String> userRoleIdList;
}
