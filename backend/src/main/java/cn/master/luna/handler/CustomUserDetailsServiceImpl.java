package cn.master.luna.handler;

import cn.master.luna.entity.SystemUser;
import cn.master.luna.entity.SystemUserRole;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.luna.entity.table.SystemUserRoleTableDef.SYSTEM_USER_ROLE;
import static cn.master.luna.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * @author Created by 11's papa on 2025/7/2
 */
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = QueryChain.of(SystemUser.class).where(SystemUser::getName).eq(username)
                .oneOpt()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<String> roleIds = QueryChain.of(SystemUserRole.class).select(SYSTEM_USER_ROLE.ID).from(SYSTEM_USER_ROLE)
                .innerJoin(USER_ROLE_RELATION).on(SYSTEM_USER_ROLE.ID.eq(USER_ROLE_RELATION.ROLE_ID))
                .where(USER_ROLE_RELATION.USER_ID.eq(systemUser.getId()))
                .listAs(String.class);
        return new org.springframework.security.core.userdetails.User(
                systemUser.getName(), systemUser.getPassword(), roleIds.stream().map(SimpleGrantedAuthority::new).toList()
        );
    }
}
