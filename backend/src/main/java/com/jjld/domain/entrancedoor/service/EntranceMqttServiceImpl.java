package com.jjld.domain.entrancedoor.service;

import com.jjld.domain.house.entity.EntranceCard;

public interface EntranceMqttServiceImpl {

    // 입주민 카드 조회
    boolean handleCard(Integer houseDong, String cardUid);

    // 공동현관 비밀번호 조회
    boolean handlePass(Integer houseHo, String entrancePass);
}
