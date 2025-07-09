package cn.master.luna.entity.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限所属资源，例如 SYSTEM_USER_ROLE
 * @author Created by 11's papa on 2025/7/8
 */
@Data
public class UserRoleResource implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Boolean license = false;
}
