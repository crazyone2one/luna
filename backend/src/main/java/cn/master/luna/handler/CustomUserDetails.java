package cn.master.luna.handler;

import cn.master.luna.entity.SystemUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author Created by 11's papa on 2025/7/9
 */
public class CustomUserDetails implements UserDetails {
    private final SystemUser user;

    public CustomUserDetails(SystemUser user) {
        this.user = user;
    }

    public static CustomUserDetails build(SystemUser user) {
        return new CustomUserDetails(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getRoleIds().stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.user.getEnable();
    }

    public String getId() {
        return this.user.getId();
    }
    public String getName() {
        return this.user.getName();
    }
    public String getUserProjectId(){
        return this.user.getLastProjectId();
    }
    public String getUserOrganizationId(){
        return this.user.getLastOrganizationId();
    }
}
