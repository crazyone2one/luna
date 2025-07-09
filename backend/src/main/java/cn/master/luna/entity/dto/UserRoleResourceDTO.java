package cn.master.luna.entity.dto;

import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.UserRolePermission;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/8
 */
@Data
public class UserRoleResourceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UserRoleResource resource;
    private List<UserRolePermission> permissions;
    private String type;

    private SystemUserRole userRole;
    private List<UserRolePermission> userRolePermissions;
}
