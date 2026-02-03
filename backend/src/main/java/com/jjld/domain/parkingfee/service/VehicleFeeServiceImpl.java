package com.jjld.domain.parkingfee.service;

import com.jjld.domain.cargate.dao.ParkingSessionDAO;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.parkingfee.dao.ParkingFeeDAO;
import com.jjld.domain.parkingfee.dto.AllInOneChargeViewResponse;
import com.jjld.domain.parkingfee.dto.SimpleRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class VehicleFeeServiceImpl implements VehicleFeeService {
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
        System.out.println("countByToday: " + countByToday);

        // 이번달 누적금액
        long countByThisMonth = parkingFeeDAO.getCountByType(thisMonthStart, thisMonthEnd);
        System.out.println("countByThisMonth: " + countByThisMonth);

        // 이번년도 누적금액
        long countByThisYear = parkingFeeDAO.getCountByType(thisYearStart, thisYearEnd);
        System.out.println("countByThisYear: " + countByThisYear);

        // 월평균 금액
        BigDecimal monthAverageCount = parkingFeeDAO.getMonthAverageCount();
        System.out.println("monthAverageCount: " + monthAverageCount);

        // 일일 최고금액
        long dayTopCount = parkingFeeDAO.getDayTopCount();
        System.out.println("dayTopCount: " + dayTopCount);

        // 월평균 방문차량 수
        long regisAverageCount = parkingSessionDAO.getUnRegisAverageCount(VehicleType.UNREGISTERED, start);
        System.out.println("regisAverageCount: " + regisAverageCount);

        return AllInOneChargeViewResponse.builder()
                .todayCount(countByToday)
                .thisMonthCount(countByThisMonth)
                .thisYearCount(countByThisYear)
                .monthAverageCount(monthAverageCount)
                .dayTopCount(dayTopCount)
                .unRegisAverageCount(regisAverageCount)
                .build();
    }
}
