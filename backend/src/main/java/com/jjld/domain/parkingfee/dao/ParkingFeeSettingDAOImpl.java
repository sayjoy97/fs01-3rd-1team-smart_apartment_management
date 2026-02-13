package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;
import com.jjld.domain.parkingfee.repository.ParkingFeeSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ParkingFeeSettingDAOImpl implements ParkingFeeSettingDAO {
    private final ParkingFeeSettingRepository feeSettingRepository;

    // 특정 날짜에 적용된 주차요금 정보 조회
    @Override
    public ParkingFeeSetting findAppliedSetting(LocalDateTime targetDate) {
        return feeSettingRepository.findAppliedSetting(targetDate);
    }

    // 가장 최근 적용했던 요금설정 조회
    @Override
    public ParkingFeeSetting getFeeSetting() {
        return feeSettingRepository.findFirstByActiveTrue()
                .orElseThrow(()-> new IllegalArgumentException("활성화된 정보없음"));
    }

    // 요금설정 수정
    @Override
    public ParkingFeeSetting createFeeSetting(ParkingFeeSetting parkingFeeSetting) {
        ParkingFeeSetting beforeSetting = getFeeSetting();
        beforeSetting.setActive(false);
        return feeSettingRepository.save(parkingFeeSetting);
    }
}
