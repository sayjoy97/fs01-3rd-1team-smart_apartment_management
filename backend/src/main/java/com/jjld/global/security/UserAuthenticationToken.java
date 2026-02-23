package com.jjld.global.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserAuthenticationToken extends UsernamePasswordAuthenticationToken {
    // 로그인 시 사용 (인증 전)
    public UserAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    // 인증 성공 후 사용 (인증 완료 상태)
    public UserAuthenticationToken(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(principal, credentials, authorities);
    }
}
