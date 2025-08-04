package cn.master.luna.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Created by 11's papa on 2025/8/4
 */
@Data
public class UserTemplate implements Serializable {
    @NotBlank(message = "{user.name.not_blank}")
    @Size(min = 1, max = 255, message = "{user.name.length_range}")
    @ExcelProperty(index = 0)
    private String name;

    @NotBlank(message = "{user.email.not_blank}")
    @Size(min = 1, max = 64, message = "{user.email.length_range}")
    @Email(message = "{user.email.invalid}")
    @ExcelProperty(index = 1)
    private String email;

    @ExcelProperty(index = 2)
    @Size(min = 1, max = 11, message = "{user.phone.error}")
    @Pattern(regexp = "^[0-9]*[1-9][0-9]*$", message = "{user.phone.error}")
    private String phone;

    @ExcelProperty(index = 3)
    private String workspaceName;
}
