package com.jjld.domain.house.controller;


import com.jjld.domain.house.dto.login.AccountUserDetail;
import com.jjld.domain.house.dto.login.CustomWebAuthenticationDetails;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;
import com.jjld.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account/api")
@RequiredArgsConstructor
public class AccountController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getHouseholderEmail(), request.getPassword());

        token.setDetails(request);

        // 인증 수행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
        AccountUserDetail accountUserDetail = (AccountUserDetail) authentication.getPrincipal();

        String jwtToken = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwtToken.trim());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(Map.of(
                        "accessToken", jwtToken,
                        "username", accountUserDetail.getUsername(),
                        "roles", accountUserDetail.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()));
    }
}
