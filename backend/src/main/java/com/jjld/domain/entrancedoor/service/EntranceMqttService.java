package com.jjld.domain.entrancedoor.service;

public interface EntranceMqttService {
    // 입주민 카드 조회
    boolean handleCard(Integer houseDong, String cardUid);

    // 공동현관 비밀번호 조회
    boolean handlePass(Integer houseDong,Integer houseHo, String rawPass);
}
