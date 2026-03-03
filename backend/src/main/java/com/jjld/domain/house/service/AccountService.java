package com.jjld.domain.house.service;

import com.jjld.domain.admin.dto.TokenRes;
import com.jjld.domain.house.dto.login.UserHouseDetailResponse;
import com.jjld.domain.house.dto.login.UserLoginRequest;
import com.jjld.domain.house.dto.login.UserLoginResponse;

public interface AccountService {
    // 비밀번호 변경
    void changePassword(Long accountId, String currentPassword, String newPassword);

    // 로그인 유저 정보
    UserHouseDetailResponse getDetail(Long houseId);
    Long getMyHouse(String householderEmail);

    // 유저 로그인
    UserLoginResponse login(UserLoginRequest request);

    // 토큰 재발급
    TokenRes refresh(String refreshToken);
}
