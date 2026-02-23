package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.EnergyUsageSummaryCreateRequest;
import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.EnergyDeviceRepository;
import com.jjld.domain.energy.repository.EnergyPolicyRepository;
import com.jjld.domain.energy.repository.EnergyUsageSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnergyUsageSummaryServiceImpl implements EnergyUsageSummaryService {
    private final EnergyDeviceRepository energyDeviceRepository;
    private final EnergyUsageSummaryRepository energyUsageSummaryRepository;
    private final EnergyAnalysisService energyAnalysisService;
    private final EnergyPolicyRepository energyPolicyRepository;
    private final EnergyExpectedKwhService energyExpectedKwhService;

    @Override
    public void createSummary(EnergyUsageSummaryCreateRequest request) {
        // 0) 기본값/필수값 방어 (periodDate는 무조건 필요)
        if (request.getPeriodDate() == null) {
            throw new IllegalArgumentException("periodDate는 필수입니다.");}
        // 1) 설비 조회
        EnergyDevice device = energyDeviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));
        // 2) TIME_SLOT이면 timeSlot 필수
        if (request.getPeriodType() == PeriodType.TIME_SLOT && request.getTimeSlot() == null) {
            throw new IllegalArgumentException("TIME_SLOT 기간에는 timeSlot이 필요합니다.");
        }
        // 3) 활성 정책 조회 (expected 계산에 필요)
        EnergyPolicy policy = energyPolicyRepository.findTopByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException("활성 정책이 없습니다."));
        double actualKwh = request.getActualKwh();
        // 4) expectedKwh 결정
        double expectedKwh = energyExpectedKwhService.calculateExpectedKwh(
                device,
                policy,
                request.getPeriodType(),
                request.getPeriodDate(),
                request.getTimeSlot(),
                actualKwh
        );
        // 5) 낭비량 계산(음수 방지)
        double wasteKwh = Math.max(actualKwh - expectedKwh, 0.0);

        // 6) UsageSummary 저장
        EnergyUsageSummary summary = EnergyUsageSummary.builder()
                .energyDevice(device)
                .periodType(request.getPeriodType())
                .periodDate(request.getPeriodDate())
                .timeSlot(request.getTimeSlot())
                .actualKwh(actualKwh)
                .expectedKwh(expectedKwh)
                .wasteKwh(wasteKwh)
                .build();
        energyUsageSummaryRepository.save(summary);

        // 7) 저장 직후 분석 트리거
        energyAnalysisService.analyzeDevice(device.getDeviceId());
    }
}
