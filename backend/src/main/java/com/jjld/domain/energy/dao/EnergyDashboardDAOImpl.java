package com.jjld.domain.energy.dao;

import com.jjld.domain.energy.entity.Enum.AnalysisStatus;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.EnergyAnalysisRepository;
import com.jjld.domain.energy.repository.EnergyDeviceRepository;
import com.jjld.domain.energy.repository.EnergyUsageSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Repository
@RequiredArgsConstructor
public class EnergyDashboardDAOImpl implements EnergyDashboardDAO {
    private final EnergyUsageSummaryRepository usageSummaryRepository;
    private final EnergyAnalysisRepository analysisRepository;
    private final EnergyDeviceRepository deviceRepository;

    @Override
    public double findMonthlyUsage(YearMonth yearMonth) {
        LocalDate date = yearMonth.atDay(1);
        return usageSummaryRepository
                .sumActualKwhByPeriod(PeriodType.MONTHLY, date);
    }

    @Override
    public long countTodayCheckRequired() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return analysisRepository
                .countByStatusToday(AnalysisStatus.CHECK_REQUIRED, start, end);
    }

    @Override
    public double sumPossibleSavingCost() {
        return analysisRepository.sumEstimatedWasteCost(AnalysisStatus.CHECK_REQUIRED);
    }

    @Override
    public long countCheckRequiredDevices() {
        return deviceRepository.countByDeviceStatus(DeviceStatus.CHECK_REQUIRED);
    }

    @Override
    public long countCheckingDevices() {
        return deviceRepository.countByDeviceStatus(DeviceStatus.CHECKING);
    }

    @Override
    public long countNormalDevices() {
        return deviceRepository.countByDeviceStatus(DeviceStatus.NORMAL);
    }
}
