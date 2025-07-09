package cn.master.luna.entity.dto;

import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.UserRoleRelation;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/8
 */
@Data
public class UserRolePermissionDTO {
    List<UserRoleResourceDTO> list = new ArrayList<>();
    List<SystemUserRole> userRoles = new ArrayList<>();
    List<UserRoleRelation> userRoleRelations = new ArrayList<>();
}
