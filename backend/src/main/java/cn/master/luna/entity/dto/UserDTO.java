package cn.master.luna.entity.dto;

import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.UserRoleRelation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/8
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class UserDTO extends SystemUser {
    private List<SystemUserRole> userRoles = new ArrayList<>();
    private List<UserRoleRelation> userRoleRelations = new ArrayList<>();
    private List<UserRoleResourceDTO> userRolePermissions = new ArrayList<>();
}
