package com.jjld.domain.noise.dao;

import java.time.LocalDate;

// 상단 요약 카드 관련 DAO -> 숫자 요약!!
public interface NoiseDashboardDAO {
    // 오늘 발생한 소음 이벤트 조회
    long findNoiseEvnetToday(LocalDate today);

    // 오늘 정책 위반 의심 소음 이벤트 조회
    long findPolicyBreakEvnetToday(LocalDate today);

    // 현재 승인 대기 중인 소음 이벤트 조회
    long countWaitingNoiseEvent();

    // 현재 시간대 판단(주간.야간)
    String findCurrentTimeZone();
}
