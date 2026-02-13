package com.jjld.domain.admin.security;

import com.jjld.domain.admin.entity.Admin;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class AdminUserDetail implements UserDetails {
    private final Admin admin;
    private final Collection<? extends GrantedAuthority> authorities;

    public Admin getAdmin() {
        return admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return admin.getAdminPass();
    }

    @Override
    public String getUsername() {
        return admin.getAdminLoginId();
    }
}
