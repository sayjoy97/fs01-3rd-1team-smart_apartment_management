package com.jjld.domain.house.controller;

import com.jjld.domain.admin.dto.TokenRes;
import com.jjld.domain.house.dto.login.*;
import com.jjld.domain.house.entity.Account;
import com.jjld.domain.house.service.AccountService;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
