package com.jjld.domain.noise.dao;

import com.jjld.domain.noise.entity.Enum.NoisePattern1;
import com.jjld.domain.noise.entity.Enum.SensorType;

import java.time.LocalDate;
import java.util.Map;

// 소음 통계/그래프 데이터 조회 DAO -> 그래프에 필요한 숫자데이터
public interface NoiseStatisticsDAO {
    // 특정 날짜의 시간대별 소음 발생 건수 조회
    Map<Integer, Long> countNoiseEventByHour(LocalDate date);

    // 특정 날짜의 시간대별 정책 위반 건수 조회
    Map<Integer, Long> countPolicyBreakByHour(LocalDate date);

    // 특정 기간 내 센서 신호 유형 분포 조회(원그래프)
    Map<SensorType, Long> countSensorType(LocalDate date);

    // 특정 기간내 소음 패턴 발생빈도 조회(레이더차트)
    Map<NoisePattern1, Long> countNoisePattern(LocalDate date);
}
