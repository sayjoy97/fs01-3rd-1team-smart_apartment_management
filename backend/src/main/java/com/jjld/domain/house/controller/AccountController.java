package com.jjld.domain.house.controller;


import com.jjld.domain.admin.dto.TokenRes;
import com.jjld.domain.admin.entity.RefreshToken;
import com.jjld.domain.admin.repository.RefreshTokenRepository;
import com.jjld.domain.house.dto.login.*;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.service.AccountService;
import com.jjld.domain.house.service.AccountServiceImpl;
import com.jjld.domain.house.service.HouseService;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import com.jjld.global.response.ApiResponse;
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
    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        UserLoginResponse response = accountService.login(request);

        return ResponseEntity.ok(response);
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRes>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ){
        if(refreshToken == null){
            throw new UnauthorizedException(ErrorCode.INTERNAL_SERVER_ERROR, "토큰을 찾을 수 없습니다.");
        }

        TokenRes tokenRes = accountService.refresh(refreshToken);

        return ResponseEntity.ok(ApiResponse.success(tokenRes));

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
