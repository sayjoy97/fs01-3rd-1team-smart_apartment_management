package com.jjld.domain.house.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountDetailsService implements UserDetailsService {
    private final AccountRepository repository;
    private final AdminDAO adminDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account entity = repository.findByHouseholderEmail(username);

        if(entity == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(entity.getRole()));
        return new AccountUserDetail(entity, roles);
    }
}
