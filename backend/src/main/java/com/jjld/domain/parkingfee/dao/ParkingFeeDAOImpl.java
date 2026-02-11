package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.parkingfee.entity.ParkingFeeHistory;
import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;
import com.jjld.domain.parkingfee.repository.ParkingFeeHistoryRepository;
import com.jjld.domain.parkingfee.repository.ParkingFeeSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ParkingFeeDAOImpl implements ParkingFeeDAO {
    private final ParkingFeeHistoryRepository feeHistoryRepository;
    private final ParkingFeeSettingRepository parkingFeeSettingRepository;

    // 활성화된 주차요금 1건 조회
    @Override
    public ParkingFeeSetting findByFirstActive() {
        return parkingFeeSettingRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new IllegalArgumentException("활성화된 정보없음"));
    }

    // 조건 날짜별 누적금액 조회
    @Override
    public long getCountByType(LocalDateTime start, LocalDateTime end) {
        ParkingStatus out = ParkingStatus.OUT;
        return feeHistoryRepository.getCountBySettingDay(out, start, end);
    }

    // 요금정산 내용추가
    @Override
    public void createFeeHistory(ParkingFeeHistory feeHistoryEntity) {
        feeHistoryRepository.save(feeHistoryEntity);
    }
}
