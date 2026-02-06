package com.jjld.domain.parkingfee.service;

import com.jjld.domain.cargate.dao.ParkingSessionDAO;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.parkingfee.dao.ParkingFeeDAO;
import com.jjld.domain.parkingfee.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
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

    // 최근 12개월 월별 누적금액 조회
    @Override
    public List<MonthlyTotalResponse> getMonthlyTotal() {
        int month = 11;
        LocalDate startMonth = today.minusMonths(month);

        Map<YearMonth, SelectedStat> monthlyRunningTotal = parkingFeeDAO.getMonthlyRunningTotal(startMonth.atStartOfDay());

        List<MonthlyTotalResponse> result = new ArrayList<>();

        for(int i=0; i<=month; i++){
            LocalDate selectedMonth = startMonth.plusMonths(i);

            YearMonth yearMonth = YearMonth.of(selectedMonth.getYear(), selectedMonth.getMonth());

            SelectedStat monthlyStat = monthlyRunningTotal.get(yearMonth);
            long monthlySum = monthlyStat != null ? monthlyStat.getTotalSum() : 0L;
            double monthlyAvg = monthlyStat != null ? monthlyStat.getTotalAvg() : 0.0;

            MonthlyTotalResponse response = MonthlyTotalResponse.builder()
                    .month(yearMonth)
                    .monthlySum(monthlySum)
                    .monthlyAvg(monthlyAvg)
                    .build();

            result.add(response);
        }
        return result;
    }

    // 최근 3년간 연간 누적금액 및 연간평균 조회
    @Override
    public List<YearTotalResponse> getYearTotal() {
        LocalDate startYear = today.minusYears(2);

        Map<Year, SelectedStat> yearlyRunningTotal = parkingFeeDAO.getYearlyRunningTotal(startYear.atStartOfDay());

        List<YearTotalResponse> result = new ArrayList<>();

        for(int i=0; i<=(today.getYear() - startYear.getYear()); i++){
            LocalDate selectedYear = startYear.plusYears(i);

            Year year = Year.of(selectedYear.getYear());
            SelectedStat yearlyStat = yearlyRunningTotal.get(year);

            long yearlySum = yearlyStat != null ? yearlyStat.getTotalSum() : 0L;
            double yearlyAvg = yearlyStat != null ? yearlyStat.getTotalAvg() : 0.0;

            YearTotalResponse response = YearTotalResponse.builder()
                    .year(year)
                    .yearTotalSum(yearlySum)
                    .yearTotalAvg(yearlyAvg)
                    .build();

            result.add(response);
        }

        return result;
    }
}
