package com.jjld.domain.house.controller;


import com.jjld.domain.house.dto.login.*;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.service.AccountService;
import com.jjld.domain.house.service.AccountServiceImpl;
import com.jjld.domain.house.service.HouseService;
import com.jjld.global.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account/api")
@RequiredArgsConstructor
public class AccountController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final HouseService houseService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getHouseholderEmail(), request.getPassword());

        token.setDetails(request);

        // 인증 수행
        Authentication authentication =
                authenticationManager.authenticate(token);

        AccountUserDetail accountUserDetail =
                (AccountUserDetail) authentication.getPrincipal();

        String jwtToken = tokenProvider.createToken(authentication);

        String role = accountUserDetail.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        boolean firstLogin =
                accountUserDetail.getAccount().isFirstLogin();

        UserLoginResponse response =
                new UserLoginResponse(
                        jwtToken,
                        accountUserDetail.getUsername(),
                        role,
                        firstLogin
                );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwtToken.trim());

        return ResponseEntity.ok(response);
    }

    // 비밀번호 변경
    @PostMapping("/password/change")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> changePassword(
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal AccountUserDetail userDetail){

        if(userDetail == null){
            return ResponseEntity.status(401).body("로그인 필요");
        }

        try{
            Account account = userDetail.getAccount();
            accountService.changePassword(
                    account.getAccountId(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok("비밀번호 변경 성공");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 로그인 유저 정보
    @GetMapping("/house")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getMyHouse(
            @AuthenticationPrincipal AccountUserDetail userDetail
            ){
        if(userDetail == null){
            return ResponseEntity.status(401).body("로그인 필요");
        }

        Long houseId = accountService.getMyHouse(userDetail.getHouseholderEmail());
        UserHouseDetailResponse response = accountService.getDetail(houseId);

        return ResponseEntity.ok(response);
    }
}
