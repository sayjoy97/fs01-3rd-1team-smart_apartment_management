package com.jjld.domain.energy.dao;

import java.time.YearMonth;

public interface EnergyDashboardDAO {
    // 이번 달 총 사용량
    double findMonthlyUsage(YearMonth yearMonth);

    // 오늘 점검 필요 건수
    long countTodayCheckRequired();

    // 예상 절감 가능 비용
    double sumPossibleSavingCost();

    long countCheckRequiredDevices();

    long countCheckingDevices();
}
