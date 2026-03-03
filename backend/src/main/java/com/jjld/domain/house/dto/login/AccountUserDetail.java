package com.jjld.domain.house.dto.login;

import com.jjld.domain.house.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AccountUserDetail implements UserDetails {
    private final Account account;
    private final Collection<? extends GrantedAuthority> authorities;

    public AccountUserDetail(Account account) {
        this.account = account;
        // account에 저장된 role을 바탕으로 권한 리스트 생성
        // 만약 role이 String이라면: new SimpleGrantedAuthority(account.getRole())
        // 만약 role이 Enum이라면: new SimpleGrantedAuthority(account.getRole().name())
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority(account.getRole())
        );
    }

    public AccountUserDetail(Account account, Collection<? extends GrantedAuthority> authorities){
        this.account = account;
        this.authorities = authorities;
    }

    public Account getAccount() {
        return account;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getHouseholderEmail();
    }

    public String getHouseholderEmail(){
        return account.getHouseholderEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
