package com.jjld.domain.house.dto.login;

import com.jjld.domain.house.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AccountUserDetail implements UserDetails {

    private final Account account;
    private final Collection<? extends GrantedAuthority> authorities;

    public AccountUserDetail(Account account, Collection<? extends GrantedAuthority>authorities){
        this.account = account;
        this.authorities = authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getHouseholderEmail();
    }
}
