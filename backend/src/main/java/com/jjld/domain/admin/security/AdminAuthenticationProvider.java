package com.jjld.domain.admin.security;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.house.service.AccountDetailsService;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminAuthenticationProvider implements AuthenticationProvider {
    private final AccountDetailsService accountDetailsService;
    private final AdminDAO adminDAO;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Admin admin = adminDAO.findByAdminLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("ROLE_" + admin.getAdminRole().name()));

        if (!passwordEncoder.matches(password, admin.getAdminPass())) {
            throw new NotFoundException(ErrorCode.INVALID_CREDENTIALS, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        AdminUserDetail adminDetail = new AdminUserDetail(admin, roles);

        return new UsernamePasswordAuthenticationToken(
                adminDetail, null, adminDetail.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
