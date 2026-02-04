package com.jjld.domain.parkingfee.dao;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;
import com.jjld.domain.parkingfee.repository.ParkingFeeHistoryRepository;
import com.jjld.domain.parkingfee.repository.ParkingFeeSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 월평균 금액 조회
    @Override
    public BigDecimal getMonthAverageCount() {
        return feeHistoryRepository.findAverageBy12Month();
    }

    // 일일 최고금액 조회
    @Override
    public long getDayTopCount() {
        return feeHistoryRepository.findMaxCharge();
    }

    // 최근 30일 일별 누적금액 조회
    @Override
    public Map<LocalDate, Long> getDailyRunningTotal(LocalDateTime start) {
        List<Object[]> dailyTotalAmount = feeHistoryRepository.findDailyTotalAmount(start);

        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] obj : dailyTotalAmount) {
            map.put(
                    ((Date) obj[0]).toLocalDate(), // sql문에서 받은 java.sql.Date를 LocalDate로 변환
                    (Long) obj[1] // 해당 날짜 값 받아오기
            );
        }

        return map;
    }

    // 최근 12주 주간별 누적금액 조회
    @Override
    public List<Long> getWeeklyRunningTotal() {
        return List.of();
    }

    // 최근 12개월 월간별 누적금액 조회
    @Override
    public List<Long> getMonthlyRunningTotal() {
        return List.of();
    }
}
