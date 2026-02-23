package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dao.EnergyDashboardDAO;
import com.jjld.domain.energy.dto.EnergyDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnergyDashboardServiceImpl implements EnergyDashboardService {
    private final EnergyDashboardDAO energyDashboardDAO;

    @Override
    public EnergyDashboardResponse getDashboard() {
        YearMonth now = YearMonth.now();
        double monthlyUsage = energyDashboardDAO.findMonthlyUsage(now);
        long checkRequired = energyDashboardDAO.countCheckRequiredDevices();
        long checking = energyDashboardDAO.countCheckingDevices();
        long normal = energyDashboardDAO.countNormalDevices();
        double possibleSaving = energyDashboardDAO.sumPossibleSavingCost();
        return EnergyDashboardResponse.builder()
                .monthlyUsageKwh(monthlyUsage)
                .checkRequiredCount(checkRequired)
                .checkingCount(checking)
                .normalCount(normal)
                .possibleSavingCost(possibleSaving)
                .build();
    }
}
