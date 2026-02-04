package com.jjld.domain.parkingfee.service;

import com.jjld.domain.cargate.dao.ParkingSessionDAO;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.parkingfee.dao.ParkingFeeDAO;
import com.jjld.domain.parkingfee.dto.AllInOneChargeViewResponse;
import com.jjld.domain.parkingfee.dto.Daily30TotalResponse;
import com.jjld.domain.parkingfee.dto.SimpleRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParkingFeeServiceImpl implements ParkingFeeService {
    private final ParkingFeeDAO parkingFeeDAO;
    private final ParkingSessionDAO parkingSessionDAO;

    // 오늘 날짜
    LocalDate today = LocalDate.now();


    // 차량 출입관리 페이지 출력용 금일+이번달 요금누적 조회
    @Override
    public SimpleRateResponse getRateByType() {
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        LocalDateTime thisMonthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime thisMonthEnd = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);

        long countByToday = parkingFeeDAO.getCountByType(todayStart, todayEnd);
        long countByThisMonth = parkingFeeDAO.getCountByType(thisMonthStart, thisMonthEnd);

        return SimpleRateResponse.builder()
                .todayRate(countByToday)
                .thisMonthRate(countByThisMonth)
                .build();
    }

    // 요금관리 페이지 상단 통합조회
    @Override
    public AllInOneChargeViewResponse getAllInOneChargeView() {
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime thisMonthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime thisMonthEnd = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);
        LocalDateTime thisYearStart = today.withDayOfYear(1).atStartOfDay();
        LocalDateTime thisYearEnd = today.withDayOfYear(today.lengthOfYear()).atTime(LocalTime.MAX);

        LocalDateTime start = today.withDayOfMonth(1).minusMonths(11).atStartOfDay();


        // 금익 누적조회
        long countByToday = parkingFeeDAO.getCountByType(todayStart, todayEnd);

        // 이번달 누적금액
        long countByThisMonth = parkingFeeDAO.getCountByType(thisMonthStart, thisMonthEnd);
        // 이번년도 누적금액
        long countByThisYear = parkingFeeDAO.getCountByType(thisYearStart, thisYearEnd);

        // 월평균 금액
        BigDecimal monthAverageCount = parkingFeeDAO.getMonthAverageCount();

        // 일일 최고금액
        long dayTopCount = parkingFeeDAO.getDayTopCount();

        // 월평균 방문차량 수
        long regisAverageCount = parkingSessionDAO.getUnRegisAverageCount(VehicleType.UNREGISTERED, start);

        return AllInOneChargeViewResponse.builder()
                .todayCount(countByToday)
                .thisMonthCount(countByThisMonth)
                .thisYearCount(countByThisYear)
                .monthAverageCount(monthAverageCount)
                .dayTopCount(dayTopCount)
                .unRegisAverageCount(regisAverageCount)
                .build();
    }

    // 최근 30일 일별 누적금액 조회
    @Override
    public List<Daily30TotalResponse> getDaily30Total() {
        LocalDate startDate = today.minusDays(29);
        Map<LocalDate, Long> dailyRunningTotal = parkingFeeDAO.getDailyRunningTotal(startDate.atStartOfDay());

        List<Daily30TotalResponse> result = new ArrayList<>();

        for(int i=0; i<30; i++){
            LocalDate date = startDate.plusDays(i);
            Long amount = dailyRunningTotal.getOrDefault(date, 0L);

            result.add(
                    Daily30TotalResponse.builder()
                            .date(date)
                            .amount(amount)
                            .build()
            );
        }

        return result;
    }
}
