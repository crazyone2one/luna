package cn.master.luna.entity.dto;

import cn.master.luna.entity.SystemOrganization;
import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserTableResponse extends SystemUser {
    @Schema(description = "用户所属组织")
    private List<SystemOrganization> organizationList = new ArrayList<>();
    @Schema(description = "用户所属用户组")
    private List<SystemUserRole> userRoleList = new ArrayList<>();

    public void setOrganization(SystemOrganization organization) {
        if (!organizationList.contains(organization)) {
            organizationList.add(organization);
        }
    }

    public void setUserRole(SystemUserRole userRole) {
        if (!userRoleList.contains(userRole)) {
            userRoleList.add(userRole);
        }
    }
}
