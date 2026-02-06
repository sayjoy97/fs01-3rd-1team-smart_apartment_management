package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.parkingfee.dto.SelectedStat;
import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;

import java.math.BigDecimal;
import java.time.*;
import java.util.Map;

public interface ParkingFeeDAO {

    // 활성화된 주차요금 1건 조회
    ParkingFeeSetting findByFirstActive();

    // 조건 날짜별 누적금액 조회
    long getCountByType(LocalDateTime start, LocalDateTime end);

    // 매월 누적 금액 조회
    BigDecimal getMonthAverageCount();

    // 일일 최고금액 조회
    long getDayTopCount();

    // 최근 30일 일별 누적금액 조회
    Map<LocalDate, Long> getDailyRunningTotal(LocalDateTime start);

    // 최근 12개월 월별 누적금액 및 월간평균 조회
    Map<YearMonth, SelectedStat> getMonthlyRunningTotal(LocalDateTime startMonth);

    // 최근 n년간 연간 누적금액 및 연간평균 조회
    Map<Year, SelectedStat> getYearlyRunningTotal(LocalDateTime startYear);

}
