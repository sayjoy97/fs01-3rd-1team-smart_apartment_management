package com.jjld.domain.admin.service;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.repository.AdminRepository;
import com.jjld.domain.admin.security.AdminUserDetail;
import com.jjld.global.security.AdminAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminAuthenticationProvider implements AuthenticationProvider {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {

        String loginId = authentication.getName();
        String password = authentication.getCredentials().toString();

        Admin admin = adminRepository.findByAdminLoginId(loginId)
                .orElseThrow(() -> new BadCredentialsException("관리자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, admin.getAdminPass())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + admin.getAdminRole().name()));

        AdminUserDetail adminUserDetail = new AdminUserDetail(admin, authorities);

        return new AdminAuthenticationToken(
                adminUserDetail,
                null,
                adminUserDetail.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AdminAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
