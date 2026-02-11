package com.jjld.domain.energy.service;

import com.jjld.domain.energy.entity.EnergyAnalysis;
import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.AnalysisStatus;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import com.jjld.domain.energy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class EnergyAnalysisServiceImpl implements EnergyAnalysisService {
    private final EnergyDeviceRepository energyDeviceRepository;
    private final EnergyUsageSummaryRepository energyUsageSummaryRepository;
    private final EnergyPolicyRepository energyPolicyRepository;
    private final EnergyAnalysisRepository energyAnalysisRepository;
    private final EnergySavingResultRepository energySavingResultRepository;

    @Override
    public void analyzeDevice(Long deviceId) {
        // 1. 설비 조회
        EnergyDevice device = energyDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));

        // 2. 활성 정책 조회
        EnergyPolicy policy = energyPolicyRepository.findByIsActiveTrue()
                .orElseThrow(() -> new IllegalStateException("활성 정책이 없습니다."));

        // 3. 최신 UsageSummary 조회
        EnergyUsageSummary summary = energyUsageSummaryRepository
                .findTopByEnergyDeviceOrderByCreatedAtDesc(device)
                .orElseThrow(() -> new IllegalStateException("사용 요약 데이터가 없습니다."));

        // 4. 낭비 계산
        double wasteKwh = summary.getWasteKwh();
        double overusePercent = summary.getActualKwh() == 0 ? 0 :
                (wasteKwh / summary.getActualKwh()) * 100;

        double estimatedWasteCost = wasteKwh * policy.getCostPerKwh();

        // 5. 상태 판정
        AnalysisStatus analysisStatus;

        if (wasteKwh >= policy.getWasteThresholdKwh()
                || overusePercent >= policy.getWarningPercent()) {
            analysisStatus = AnalysisStatus.CHECK_REQUIRED;
        } else {
            analysisStatus = AnalysisStatus.NORMAL;
        }

        // 6. 분석 저장
        EnergyAnalysis analysis = EnergyAnalysis.builder()
                .energyDevice(device)
                .usageSummary(summary)
                .energyPolicy(policy)
                .overusePercent(overusePercent)
                .analysisStatus(analysisStatus)
                .estimatedWasteKwh(wasteKwh)
                .estimatedWasteCost(estimatedWasteCost)
                .causeEstimate(null)
                .breachCount24h(0)
                .singleBreachIgnored(false)
                .analyzedAt(LocalDateTime.now())
                .build();

        energyAnalysisRepository.save(analysis);

        // 7. 설비 상태 동기화 (점검 중이면 건드리지 않음)
        if (device.getDeviceStatus() != DeviceStatus.CHECKING) {
            if (analysisStatus == AnalysisStatus.CHECK_REQUIRED) {
                device.setDeviceStatus(DeviceStatus.CHECK_REQUIRED);
            } else {
                device.setDeviceStatus(DeviceStatus.NORMAL);
            }
        }
        // 8. 제어 후 절감 효과 계산
        energySavingResultRepository
                .findTopByEnergyDeviceAndAfterKwhIsNullOrderByEvaluatedAtDesc(device)
                .ifPresent(saving -> {

                    double before = saving.getBeforeKwh();
                    double after = analysis.getEstimatedWasteKwh();

                    double savedKwh = before - after;
                    if (savedKwh < 0) {
                        savedKwh = 0;
                    }

                    double savedCost = savedKwh * policy.getCostPerKwh();

                    saving.setAfterKwh(after);
                    saving.setSavedKwh(savedKwh);
                    saving.setSavedCost(savedCost);
                    saving.setEvaluatedAt(LocalDateTime.now());
                });
    }
}
