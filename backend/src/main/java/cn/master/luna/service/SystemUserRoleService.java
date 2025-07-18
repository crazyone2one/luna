package cn.master.luna.service;

import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.dto.UserSelectOption;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户组 服务层。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
public interface SystemUserRoleService extends IService<SystemUserRole> {
    List<UserSelectOption> getGlobalSystemRoleList();
}
