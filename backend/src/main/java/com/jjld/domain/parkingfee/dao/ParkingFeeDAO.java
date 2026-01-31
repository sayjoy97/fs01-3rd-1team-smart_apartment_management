package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;

import java.time.LocalDateTime;

public interface ParkingFeeDAO {

    // 활성화된 주차요금 1건 조회
    ParkingFeeSetting findByFirstActive();

    // 조건 날짜별 누적금액 조회
    long getCountByType(LocalDateTime start, LocalDateTime end);

}
