package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.EnergyMeasurementCreateRequest;
import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyMeasurement;
import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.EnergyDeviceRepository;
import com.jjld.domain.energy.repository.EnergyMeasurementRepository;
import com.jjld.domain.energy.repository.EnergyPolicyRepository;
import com.jjld.domain.energy.repository.EnergyUsageSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EnergyMeasurementServiceImpl implements EnergyMeasurementService {
    private final EnergyDeviceRepository energyDeviceRepository;
    private final EnergyMeasurementRepository energyMeasurementRepository;
    private final EnergyUsageSummaryRepository energyUsageSummaryRepository;
    private final EnergyAnalysisService energyAnalysisService;
    private final EnergyPolicyRepository energyPolicyRepository;
    private final EnergyExpectedKwhService energyExpectedKwhService;

    @Override
    public void createMeasurement(EnergyMeasurementCreateRequest request) {
        // 1. 설비 조회
        EnergyDevice device = energyDeviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new IllegalArgumentException("설비를 찾을 수 없습니다."));

        // 2. Measurement 저장
        EnergyMeasurement measurement = EnergyMeasurement.builder()
                .energyDevice(device)
                .voltage(request.getVoltage())
                .current(request.getCurrent())
                .power(request.getPower())
                .energyKwh(request.getEnergyKwh())
                .createdAt(LocalDateTime.now())
                .build();

        energyMeasurementRepository.save(measurement);

        // 3-1) 활성 정책 조회 (expected 계산용)
        EnergyPolicy policy = energyPolicyRepository.findTopByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException("활성 정책이 없습니다."));
        // 3. DAILY 기준 UsageSummary 생성/갱신
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        // 10분 단위 timeSlot: 0~143 (하루 144칸)
        int timeSlot = now.getHour() * 6 + (now.getMinute() / 10);

        EnergyUsageSummary summary = energyUsageSummaryRepository
                .findByEnergyDeviceAndPeriodTypeAndPeriodDateAndTimeSlot(device, PeriodType.TIME_SLOT, today, timeSlot)
                .orElseGet(() -> EnergyUsageSummary.builder()
                        .energyDevice(device)
                        .periodType(PeriodType.TIME_SLOT)
                        .periodDate(today)
                        .timeSlot(timeSlot)
                        .actualKwh(0.0)
                        .expectedKwh(0.0) // 아직 정책/예측 로직 확정 전이면 0으로 두고, /usage에서 채우는 구조로 유지
                        .wasteKwh(0.0)
                        .build()
                );
        // 핵심: 들어온 측정치를 "오늘 누적 사용량"에 더한다
        // 오늘 누적 actualKwh 갱신
        double updatedActualKwh = summary.getActualKwh() + request.getEnergyKwh();
        summary.setActualKwh(updatedActualKwh);
        // 3-3) expectedKwh 계산 (완성형)
        double expectedKwh = energyExpectedKwhService.calculateExpectedKwh(
                device,
                policy,
                PeriodType.TIME_SLOT,
                today,
                timeSlot,    // DAILY는 timeSlot 없음
                updatedActualKwh // fallback 계산에도 사용
        );
        summary.setExpectedKwh(expectedKwh);
        // 3-4) waste 계산(음수 방지)
        summary.setWasteKwh(Math.max(updatedActualKwh - expectedKwh, 0.0));
        energyUsageSummaryRepository.save(summary);

        EnergyUsageSummary dailySummary = energyUsageSummaryRepository
                .findByEnergyDeviceAndPeriodTypeAndPeriodDate(device, PeriodType.DAILY, today)
                .orElseGet(() -> EnergyUsageSummary.builder()
                        .energyDevice(device)
                        .periodType(PeriodType.DAILY)
                        .periodDate(today)
                        .actualKwh(0.0)
                        .expectedKwh(0.0)
                        .wasteKwh(0.0)
                        .build());

        double updatedDailyActual = dailySummary.getActualKwh() + request.getEnergyKwh();
        dailySummary.setActualKwh(updatedDailyActual);

        double dailyExpected = energyExpectedKwhService.calculateExpectedKwh(
                device, policy, PeriodType.DAILY, today, null, updatedDailyActual
        );
        dailySummary.setExpectedKwh(dailyExpected);
        dailySummary.setWasteKwh(Math.max(updatedDailyActual - dailyExpected, 0.0));
        energyUsageSummaryRepository.save(dailySummary);

// =========================
// (C) MONTHLY upsert (periodDate = 이번 달 1일)
// =========================
        LocalDate monthStart = today.withDayOfMonth(1);
        EnergyUsageSummary monthlySummary = energyUsageSummaryRepository
                .findByEnergyDeviceAndPeriodTypeAndPeriodDate(device, PeriodType.MONTHLY, monthStart)
                .orElseGet(() -> EnergyUsageSummary.builder()
                        .energyDevice(device)
                        .periodType(PeriodType.MONTHLY)
                        .periodDate(monthStart)
                        .actualKwh(0.0)
                        .expectedKwh(0.0)
                        .wasteKwh(0.0)
                        .build());

        double updatedMonthlyActual = monthlySummary.getActualKwh() + request.getEnergyKwh();
        monthlySummary.setActualKwh(updatedMonthlyActual);

        double monthlyExpected = energyExpectedKwhService.calculateExpectedKwh(
                device, policy, PeriodType.MONTHLY, monthStart, null, updatedMonthlyActual
        );
        monthlySummary.setExpectedKwh(monthlyExpected);
        monthlySummary.setWasteKwh(Math.max(updatedMonthlyActual - monthlyExpected, 0.0));
        energyUsageSummaryRepository.save(monthlySummary);
        // 4. 자동 분석 실행
        energyAnalysisService.analyzeDevice(device.getDeviceId());
    }
}