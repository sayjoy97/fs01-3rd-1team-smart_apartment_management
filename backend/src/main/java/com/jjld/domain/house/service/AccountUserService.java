package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AccountUserService extends User {

    private final UserResponse response;


    public AccountUserService(UserResponse response, String password, Collection<? extends GrantedAuthority> authorities) {
        super(response.getHouseholderEmail(), password, authorities);
        this.response = response;
    }

}
