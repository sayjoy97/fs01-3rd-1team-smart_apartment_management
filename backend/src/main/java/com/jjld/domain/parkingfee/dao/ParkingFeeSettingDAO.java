package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;

import java.time.LocalDateTime;

public interface ParkingFeeSettingDAO {

    // 특정 날짜에 적용된 주차요금 정보 조회
    ParkingFeeSetting findAppliedSetting(LocalDateTime targetDate);

    // 가장 최근 적용했던 요금설정 조회
    ParkingFeeSetting getFeeSetting();

    // 요금설정 변경
    ParkingFeeSetting createFeeSetting(ParkingFeeSetting parkingFeeSetting);
}
