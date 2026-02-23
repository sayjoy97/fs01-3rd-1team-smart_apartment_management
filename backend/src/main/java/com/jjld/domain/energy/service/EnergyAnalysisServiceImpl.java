package com.jjld.domain.energy.service;

import com.jjld.domain.energy.entity.EnergyAnalysis;
import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.AnalysisStatus;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EnergyAnalysisServiceImpl implements EnergyAnalysisService {
    private final EnergyDeviceRepository energyDeviceRepository;
    private final EnergyUsageSummaryRepository energyUsageSummaryRepository;
    private final EnergyPolicyRepository energyPolicyRepository;
    private final EnergyAnalysisRepository energyAnalysisRepository;
    private final EnergySavingResultRepository energySavingResultRepository;
    private static final Duration SAVING_WINDOW = Duration.ofMinutes(10);
    private static final int MIN_ANALYSIS_COUNT = 3;

    @Override
    public void analyzeDevice(Long deviceId) {
        // 1. 설비 조회
        EnergyDevice device = energyDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));

        // 2. 활성 정책 조회
        EnergyPolicy policy = energyPolicyRepository.findTopByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalStateException("활성 정책이 없습니다."));

        // 3. 최신 UsageSummary 조회
        EnergyUsageSummary summary = energyUsageSummaryRepository
                .findTopByEnergyDeviceAndPeriodTypeOrderByCreatedAtDesc(device, PeriodType.TIME_SLOT)
                .or(() -> energyUsageSummaryRepository.findTopByEnergyDeviceAndPeriodTypeOrderByCreatedAtDesc(device, PeriodType.DAILY))
                .or(() -> energyUsageSummaryRepository.findTopByEnergyDeviceAndPeriodTypeOrderByCreatedAtDesc(device, PeriodType.MONTHLY))
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
                    // 절감 시작 시각(=제어 시각으로 세팅한 값)
                    LocalDateTime start = saving.getControlLog().getControlledAt();
                    LocalDateTime end = start.plus(SAVING_WINDOW);
                    LocalDateTime now = LocalDateTime.now();

                    // 아직 10분이 안 지났으면 마감하지 않음
                    if (now.isBefore(end)) return;

                    // 절감 창(10분) 동안 생성된 분석만 가져오기
                    List<EnergyAnalysis> windowAnalyses =
                            energyAnalysisRepository.findByEnergyDeviceAndAnalyzedAtBetweenOrderByAnalyzedAtAsc(
                                    device, start, end
                            );

                    // 최소 3회 분석이 누적되지 않았으면 마감하지 않음
                    if (windowAnalyses.size() < MIN_ANALYSIS_COUNT) return;

                    // 3. 평균 계산
                    double avgAfter = windowAnalyses.stream()
                            .mapToDouble(EnergyAnalysis::getEstimatedWasteKwh)
                            .average()
                            .orElse(saving.getBeforeKwh());
                    double before = saving.getBeforeKwh();
                    double savedKwh = Math.max(before - avgAfter, 0.0);
                    double savedCost = savedKwh * policy.getCostPerKwh();
                    saving.setAfterKwh(avgAfter);
                    saving.setSavedKwh(savedKwh);
                    saving.setSavedCost(savedCost);
                    saving.setEvaluatedAt(now);

                    log.info("[SAVING] start={}, end={}, now={}", start, end, now);
                    log.info("[SAVING] window analyses count={}", windowAnalyses.size());
                    log.info("[SAVING] close: before={}, afterAvg={}, savedKwh={}, savedCost={}",
                            before, avgAfter, savedKwh, savedCost);
                });
    }
}
