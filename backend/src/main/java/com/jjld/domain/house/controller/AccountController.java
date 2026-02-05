package com.jjld.domain.house.controller;


import com.jjld.domain.house.dto.UserResponse;
import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.service.AccountService;
import com.jjld.global.response.ApiResponse;
import com.jjld.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/account/api")
@RequiredArgsConstructor
public class AccountController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginRequest request){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getHouseholderEmail(), request.getPassword());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.getObject();
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        AccountUserDetail accountUserDetail = (AccountUserDetail) authentication.getPrincipal();

        String result = "Fail";
        String jwtToken = "";

        if(accountUserDetail != null){
            jwtToken = tokenProvider.createToken(authentication);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer "+jwtToken);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(Map.of(
                        "accessToken", jwtToken,
                        "username",accountUserDetail.getUsername(),
                        "roles", accountUserDetail.getPassword()));

    }
}
