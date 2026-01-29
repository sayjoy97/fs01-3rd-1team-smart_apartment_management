package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.parkingfee.repository.ParkingFeeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ParkingFeeHistoryDAOImpl implements ParkingFeeHistoryDAO {
    private final ParkingFeeHistoryRepository feeHistoryRepository;

    // 조건 날짜별 누적금액 조회
    @Override
    public long getCountByType(LocalDateTime start, LocalDateTime end) {
        ParkingStatus out = ParkingStatus.OUT;
        return feeHistoryRepository.getCountBySettingDay(out, start, end);
    }
}
