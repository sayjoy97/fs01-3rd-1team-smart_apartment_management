package com.jjld.domain.house.service;

import com.jjld.domain.house.dto.HouseDetailResponse;
import com.jjld.domain.house.dto.login.UserHouseDetailResponse;

public interface AccountService {
    // 비밀번호 변경
    void changePassword(Long accountId, String currentPassword, String newPassword);

    // 로그인 유저 정보
    UserHouseDetailResponse getDetail(Long houseId);
    Long getMyHouse(String householderEmail);

}
