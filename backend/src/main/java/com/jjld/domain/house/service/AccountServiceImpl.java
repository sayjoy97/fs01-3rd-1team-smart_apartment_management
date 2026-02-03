package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.repository.AccountRepository;
import com.jjld.global.exception.house.UserAccountNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserLoginResponse login(UserLoginRequest request) {


//        Account account = repository.findByEmail(request.getHouseholderEmail());
//        if(account == null){
//            throw new UserAccountNotFound("사용자 계정 이메일을 찾을 수 없습니다.");
//        }
//        if(!passwordEncoder.matches(request.getPassword(), account.getPassword()){
//            throw new RuntimeException("비밀번호가 일치하지 않습니다");
//        }
//        if(!account.isActive()){
//            throw new RuntimeException("비활성화된 계정입니다.");
//        }
//
//        boolean isFirstLogin = account.isFirstLogin();

//        String token = jwtTokenProvider
        return null;
    }
}
