package com.jjld.domain.admin.service;

import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Admin admin = adminRepository.findByAdminLoginId(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("관리자를 찾을 수 없습니다."));

        return org.springframework.security.core.userdetails.User.builder()
                .username(admin.getAdminLoginId())
                .password(admin.getAdminPass())
                .roles(admin.getAdminRole().name())
                .build();
    }
}

