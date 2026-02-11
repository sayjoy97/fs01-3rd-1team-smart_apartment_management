package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dao.EnergyDeviceDAO;
import com.jjld.domain.energy.dto.*;
import com.jjld.domain.energy.entity.*;
import com.jjld.domain.energy.entity.Enum.AnalysisStatus;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import com.jjld.domain.energy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnergyDeviceServiceImpl implements EnergyDeviceService {
    private final EnergyDeviceDAO energyDeviceDAO;
    private final EnergyDeviceRepository energyDeviceRepository;
    private final EnergyAnalysisRepository energyAnalysisRepository;
    private final EnergyControlLogRepository energyControlLogRepository;
    private final EnergyUsageSummaryRepository energyUsageSummaryRepository;
    private final EnergySavingResultRepository energySavingResultRepository;

    @Override
    public Page<EnergyCheckRequiredDeviceResponse> getCheckRequiredDevices(Pageable pageable) {
        return energyDeviceDAO.findCheckRequiredDevices(pageable)
                .map(this::toResponse);
    }

    @Override
    public void startCheck(Long deviceId) {
        EnergyDevice device = energyDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));
        // 이미 점검 중이면 중복 변경 방지
        if (device.getDeviceStatus() == DeviceStatus.CHECKING) {
            return;
        }
        device.setDeviceStatus(DeviceStatus.CHECKING);
    }

    @Override
    public void completeCheck(Long deviceId) {
        EnergyDevice device = energyDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));
        device.setDeviceStatus(DeviceStatus.NORMAL);
        device.setLastCheckDate(LocalDate.now());
    }

    @Override
    public EnergyDeviceDetailResponse getDeviceDetail(Long deviceId) {
        EnergyDevice device = energyDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));
        // 최신 분석 조회
        EnergyAnalysis latest = energyAnalysisRepository
                .findLatestByDeviceId(deviceId, PageRequest.of(0,1))
                .stream()
                .findFirst()
                .orElse(null);

        Double monthChangeRate = calculateMonthChangeRate(device);
        String recommendedAction = generateRecommendedAction(latest);
        String expectedEffectMessage = generateExpectedEffect(latest);
        // House 정보 직접 계산
        String buildingName = null;
        String houseInfo = null;

        if (device.getHouse() != null) {
            Integer dong = device.getHouse().getHouseDong();
            Integer ho = device.getHouse().getHouseHo();
            buildingName = dong + "동";
            int floor = ho / 100;
            houseInfo = floor + "층 " + ho + "호";
        }

        return EnergyDeviceDetailResponse.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .location(device.getLocation())
                .deviceType(device.getDeviceType().name())
                .buildingName(buildingName)
                .houseInfo(houseInfo)
                .deviceStatus(device.getDeviceStatus())
                .lastCheckDate(device.getLastCheckDate())
                .estimatedWasteKwh(latest != null ? latest.getEstimatedWasteKwh() : 0)
                .overusePercent(latest != null ? latest.getOverusePercent() : 0)
                .estimatedWasteCost(latest != null ? latest.getEstimatedWasteCost() : 0)
                .monthChangeRate(monthChangeRate)
                .causeEstimate(latest != null ? latest.getCauseEstimate() : null)
                .recommendedAction(recommendedAction)
                .expectedEffectMessage(expectedEffectMessage)
                .analyzedAt(latest != null ? latest.getAnalyzedAt() : null)
                .build();
    }

    @Override
    public List<EnergyControlLogResponse> getControlLogs(Long deviceId) {
        return energyControlLogRepository
                .findByEnergyDeviceDeviceIdOrderByControlledAtDesc(deviceId)
                .stream()
                .map(log -> EnergyControlLogResponse.builder()
                        .beforeState(log.getBeforeState())
                        .afterState(log.getAfterState())
                        .adminName(log.getAdmin().getAdminName())
                        .reason(log.getReason())
                        .controlledAt(log.getControlledAt())
                        .build()
                )
                .toList();
    }

    @Override
    public Page<EnergyDeviceRowResponse> getDeviceList(DeviceStatus status, Pageable pageable) {
        Page<EnergyAnalysis> page = energyAnalysisRepository.findLatestAnalysisByStatus(status, pageable);
        return page.map(this::toRowResponse);
    }

    @Override
    public void controlDevice(Long deviceId, Boolean operate, String reason) {
        // 1. 설비 조회
        EnergyDevice device = energyDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 설비를 찾을 수 없습니다."));
        Boolean beforeState = device.getIsOperating();
        // 2. 동일 상태면 종료
        if (beforeState.equals(operate)) {
            return;
        }
        // 3. 상태 변경
        device.setIsOperating(operate);
        // 4. 제어 로그 저장
        EnergyControlLog log = EnergyControlLog.builder()
                .energyDevice(device)
                .beforeState(beforeState)
                .afterState(operate)
                .reason(reason)
                .controlledAt(LocalDateTime.now())
                .build();
        energyControlLogRepository.save(log);
        // 5. 제어 전 낭비량 조회 (절감 시작 기준의 기록만 저장)
        EnergyAnalysis latest = energyAnalysisRepository
                .findLatestByDeviceId(deviceId, PageRequest.of(0,1))
                .stream()
                .findFirst()
                .orElse(null);

        if (latest != null) {
            EnergySavingResult saving = EnergySavingResult.builder()
                    .energyDevice(device)
                    .controlLog(log)
                    .beforeKwh(latest.getEstimatedWasteKwh())
                    .afterKwh(null) // 아직 모름
                    .savedKwh(null) // 아직 계산 안함
                    .savedCost(null) // 아직 계산 안함
                    .evaluatedAt(LocalDateTime.now())
                    .build();
            energySavingResultRepository.save(saving);
        }
    }

    @Override
    public Page<EnergySavingResultResponse> getSavingResults(Long deviceId, Pageable pageable) {
        return energySavingResultRepository
                .findByEnergyDeviceDeviceIdOrderByEvaluatedAtDesc(deviceId, pageable)
                .map(result -> EnergySavingResultResponse.builder()
                        .savingId(result.getSavingId())
                        .beforeKwh(result.getBeforeKwh())
                        .afterKwh(result.getAfterKwh())
                        .savedKwh(result.getSavedKwh())
                        .savedCost(result.getSavedCost())
                        .evaluatedAt(result.getEvaluatedAt())
                        .build()
                );
    }

    private EnergyCheckRequiredDeviceResponse toResponse(EnergyAnalysis analysis) {
        EnergyDevice device = analysis.getEnergyDevice();
        return EnergyCheckRequiredDeviceResponse.builder()
                .analysisId(analysis.getAnalysisId())
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .location(device.getLocation())
                .isOperating(device.getIsOperating())
                .deviceStatus(device.getDeviceStatus())
                .analysisStatus(analysis.getAnalysisStatus())
                .estimatedWasteKwh(analysis.getEstimatedWasteKwh())
                .overusePercent(analysis.getOverusePercent())
                .estimatedWasteCost(analysis.getEstimatedWasteCost())
                .causeEstimate(analysis.getCauseEstimate())
                .analyzedAt(analysis.getAnalyzedAt())
                .build();
    }
    private EnergyDeviceRowResponse toRowResponse(EnergyAnalysis analysis) {
        EnergyDevice device = analysis.getEnergyDevice();

        Double monthChangeRate = calculateMonthChangeRate(device);

        return EnergyDeviceRowResponse.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .location(device.getLocation())
                .isOperating(device.getIsOperating())
                .deviceStatus(device.getDeviceStatus())
                .estimatedWasteKwh(analysis.getEstimatedWasteKwh())
                .overusePercent(analysis.getOverusePercent())
                .estimatedWasteCost(analysis.getEstimatedWasteCost())
                .monthChangeRate(monthChangeRate)
                .causeEstimate(analysis.getCauseEstimate())
                .analyzedAt(analysis.getAnalyzedAt())
                .build();
    }
    // 전월 대비 변화율 계산 메소드
    private Double calculateMonthChangeRate(EnergyDevice device) {

        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastMonth = thisMonth.minusMonths(1);

        Double thisWaste = energyUsageSummaryRepository
                .findByEnergyDeviceAndPeriodTypeAndPeriodDate(
                        device, PeriodType.MONTHLY, thisMonth)
                .map(EnergyUsageSummary::getWasteKwh)
                .orElse(0.0);

        Double lastWaste = energyUsageSummaryRepository
                .findByEnergyDeviceAndPeriodTypeAndPeriodDate(
                        device, PeriodType.MONTHLY, lastMonth)
                .map(EnergyUsageSummary::getWasteKwh)
                .orElse(0.0);

        if (lastWaste == 0) return 0.0;

        return ((thisWaste - lastWaste) / lastWaste) * 100;
    }
    // 권장 조치 자동 생성
    private String generateRecommendedAction(EnergyAnalysis analysis) {

        if (analysis == null) return null;

        if (analysis.getAnalysisStatus() == AnalysisStatus.CHECK_REQUIRED) {
            return "설비 점검을 수행하고 비점유 시간 자동 제어 설정을 검토하십시오.";
        }

        return "현재 정상 범위입니다.";
    }
    // 조치 시 예상 효과 메시지 생성
    private String generateExpectedEffect(EnergyAnalysis analysis) {

        if (analysis == null) return null;

        return "관리자가 문제를 해결할 경우 이 설비는 월 약 ₩"
                + Math.round(analysis.getEstimatedWasteCost())
                + " (" + Math.round(analysis.getEstimatedWasteKwh())
                + " kWh)를 절감할 가능성이 있습니다.";
    }
}
