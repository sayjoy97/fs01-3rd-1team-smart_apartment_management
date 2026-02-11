package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.EnergyUsageSummaryCreateRequest;
import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.EnergyDeviceRepository;
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

    @Override
    public void createSummary(EnergyUsageSummaryCreateRequest request) {
        // 1) 설비 조회
        EnergyDevice device = energyDeviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));

        // 2) TIME_SLOT이면 timeSlot 필수
        if (request.getPeriodType() == PeriodType.TIME_SLOT && request.getTimeSlot() == null) {
            throw new IllegalArgumentException("TIME_SLOT 기간에는 timeSlot이 필요합니다.");
        }

        // 3) 낭비량 계산(음수 방지)
        double wasteKwh = Math.max(request.getActualKwh() - request.getExpectedKwh(), 0);

        // 4) UsageSummary 저장
        EnergyUsageSummary summary = EnergyUsageSummary.builder()
                .energyDevice(device)
                .periodType(request.getPeriodType())
                .periodDate(request.getPeriodDate())
                .timeSlot(request.getTimeSlot())
                .actualKwh(request.getActualKwh())
                .expectedKwh(request.getExpectedKwh())
                .wasteKwh(wasteKwh)
                .build();

        energyUsageSummaryRepository.save(summary);

        // 5) 저장 직후 분석 트리거 (완성본 기준 흐름)
        energyAnalysisService.analyzeDevice(device.getDeviceId());
    }
}
