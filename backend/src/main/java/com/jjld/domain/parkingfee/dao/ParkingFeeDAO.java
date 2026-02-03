package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ParkingFeeDAO {

    // 활성화된 주차요금 1건 조회
    ParkingFeeSetting findByFirstActive();

    // 조건 날짜별 누적금액 조회
    long getCountByType(LocalDateTime start, LocalDateTime end);

    // 매월 누적 금액 조회
    BigDecimal getMonthAverageCount();

    // 일일 최고금액 조회
    long getDayTopCount();

    // 월평균 방문차량(미등록) <- vehicle테이블에서 할것

}
