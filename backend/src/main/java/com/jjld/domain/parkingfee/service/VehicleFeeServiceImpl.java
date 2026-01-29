package com.jjld.domain.parkingfee.service;

import com.jjld.domain.parkingfee.dao.VehicleFeeDAO;
import com.jjld.domain.parkingfee.dto.SimpleRateResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class VehicleFeeServiceImpl implements VehicleFeeService {
    private final VehicleFeeDAO vehicleFeeDAO;

    // 차량 출입관리 페이지 출력용 금일+이번달 요금누적 조회
    @Override
    public SimpleRateResponse getRateByType() {
        LocalDate today = LocalDate.now();

        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        LocalDateTime thisMonthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime thisMonthEnd = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);

        long countByToday = vehicleFeeDAO.getCountByType(todayStart, todayEnd);
        long countByThisMonth = vehicleFeeDAO.getCountByType(thisMonthStart, thisMonthEnd);

        return SimpleRateResponse.builder()
                .todayRate(countByToday)
                .thisMonthRate(countByThisMonth)
                .build();
    }
}
