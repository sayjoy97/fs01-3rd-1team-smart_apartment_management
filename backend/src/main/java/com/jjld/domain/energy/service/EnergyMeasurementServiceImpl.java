package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.EnergyMeasurementCreateRequest;
import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyMeasurement;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.EnergyDeviceRepository;
import com.jjld.domain.energy.repository.EnergyMeasurementRepository;
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

        // 3. DAILY 기준 UsageSummary 생성/갱신
        LocalDate today = LocalDate.now();

        EnergyUsageSummary summary = energyUsageSummaryRepository
                .findByEnergyDeviceAndPeriodTypeAndPeriodDate(device, PeriodType.DAILY, today)
                .orElseGet(() -> EnergyUsageSummary.builder()
                        .energyDevice(device)
                        .periodType(PeriodType.DAILY)
                        .periodDate(today)
                        .actualKwh(0.0)
                        .expectedKwh(0.0) // 아직 정책/예측 로직 확정 전이면 0으로 두고, /usage에서 채우는 구조로 유지
                        .wasteKwh(0.0)
                        .build()
                );

        // 핵심: 들어온 측정치를 "오늘 누적 사용량"에 더한다
        double updatedActualKwh = summary.getActualKwh() + request.getEnergyKwh();
        summary.setActualKwh(updatedActualKwh);

        // expected/waste는 이 단계에서 임의 계산하지 않음 (완성본 기준으로 로직 분리)
        // 나중에 /usage에서 expected를 넣거나, 정책 기반 예상치를 계산하는 서비스로 따로 뺄 것
        summary.setWasteKwh(summary.getActualKwh() - summary.getExpectedKwh());

        energyUsageSummaryRepository.save(summary);

        // 4. 자동 분석 실행
        energyAnalysisService.analyzeDevice(device.getDeviceId());
    }
}