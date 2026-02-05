package com.jjld.domain.house.service;

import com.jjld.domain.house.dao.AccountDAO;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.dto.login.UserLoginResponse;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {
    private final AccountDAO accountDAO;
    private final AccountRepository repository;
    private final ModelMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        System.out.println("로그인 시도 username = " + username);
        Account entity = repository.findByHouseholderEmail(username);
        System.out.println("조회 결과= "+entity);
        if(entity == null){
            throw new IllegalArgumentException("인증실패");
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(entity.getRole()));
        UserLoginResponse res = mapper.map(entity, UserLoginResponse.class);


        return new AccountUserDetail(entity, roles);
    }
}
