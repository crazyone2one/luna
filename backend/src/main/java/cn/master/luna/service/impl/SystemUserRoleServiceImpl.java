package cn.master.luna.service.impl;

import cn.master.luna.constants.UserRoleType;
import cn.master.luna.entity.SystemUserRole;
import cn.master.luna.entity.dto.UserSelectOption;
import cn.master.luna.mapper.SystemUserRoleMapper;
import cn.master.luna.service.SystemUserRoleService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.master.luna.constants.InternalUserRole.MEMBER;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0 2025-07-02
 */
@Service
public class SystemUserRoleServiceImpl extends ServiceImpl<SystemUserRoleMapper, SystemUserRole>  implements SystemUserRoleService{

    @Override
    public List<UserSelectOption> getGlobalSystemRoleList() {
        List<UserSelectOption> returnList = new ArrayList<>();
        List<SystemUserRole> userRoles = queryChain()
                .where(SystemUserRole::getScopeId).eq("global").and(SystemUserRole::getType).eq(UserRoleType.SYSTEM.name())
                .list();
        for (SystemUserRole userRole : userRoles) {
            UserSelectOption userRoleOption = new UserSelectOption();
            userRoleOption.setId(userRole.getId());
            userRoleOption.setName(userRole.getName());
            userRoleOption.setSelected(StringUtils.equals(userRole.getId(), MEMBER.getValue()));
            userRoleOption.setCloseable(!StringUtils.equals(userRole.getId(), MEMBER.getValue()));
            returnList.add(userRoleOption);
        }
        return returnList;
    }
}
