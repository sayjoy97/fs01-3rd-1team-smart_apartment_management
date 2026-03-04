package com.jjld.domain.noise.service;

import com.jjld.domain.house.entity.House;
import com.jjld.domain.noise.dao.NoiseEventDAO;
import com.jjld.domain.noise.dto.NoiseEventDetailResponse;
import com.jjld.domain.noise.dto.NoiseEventListResponse;
import com.jjld.domain.noise.dto.NoiseUrgentEventResponse;
import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.*;
import com.jjld.domain.noise.repository.NoiseEventProcessRepository;
import com.jjld.domain.noise.repository.NoiseHabitualZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NoiseEventServiceImpl implements NoiseEventService {
    private final NoiseEventDAO noiseEventDAO;
    private final NoiseEventProcessRepository noiseEventProcessRepository;
    private final NoisePolicyService noisePolicyService;
    private final NoiseHabitualZoneRepository noiseHabitualZoneRepository;

    //-----목록-------
    @Override
    @Transactional(readOnly = true)
    public Page<NoiseEventListResponse> getNoiseEventListResponses(
            ProcessStatus status,
            String viewMode,
            Pageable pageable) {
        NoisePolicy policy = noisePolicyService.findActiveNoisePolicy();
        String mode = (viewMode == null || viewMode.isBlank()) ? "all" : viewMode.toLowerCase();
        Page<NoiseEventProcess> page = noiseEventProcessRepository.findForList(
                status, mode, policy.getDayStartTime(),
                policy.getNightStartTime(), pageable);
        return page.map(this::toListResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoiseEventListResponse> getNoiseEventList(ProcessStatus status, String timeZone, Pageable pageable) {
        // 1) DB에서 Page<NoiseEventProcess> 가져오기 (기존 그대로)
        Page<NoiseEventProcess> processPage =
                (status == null)
                        ? noiseEventDAO.findAllNoiseEvent(pageable)
                        : noiseEventDAO.findNoiseEventByStatus(status, pageable);

        // 2) Page -> DTO 변환 (Page 유지)
        Page<NoiseEventListResponse> dtoPage = processPage.map(this::toListResponse);
        // (지금은 viewMode 파라미터가 메서드에 없으니 "전체"만 반환)
        // 나중에 viewMode 파라미터 추가하면 아래 필터 부분만 활성화하면 됨.
        return dtoPage;
    }

    //  ------ 즉시처리 -------
    @Override
    @Transactional(readOnly = true)
    public Page<NoiseUrgentEventResponse> getUrgentNoiseEventResponses(Pageable pageable) {
        // 1. 즉시 처리 필요 이벤트(Process) 조회
        Page<NoiseEventProcess> processPage = noiseEventProcessRepository.findByUrgentBreakTrueAndStatus(ProcessStatus.UNPROCESSED, pageable);
        // 2. 엔티티 → DTO 변환
        return processPage.map(this::toUrgentResponse);
    }
    // -----소음이벤트 상세조회------
    @Override
    @Transactional(readOnly = true)
    public NoiseEventDetailResponse getNoiseEventDetailResponse(Long noiseEventId) {
        // 1. 서비스 호출을 통해 소음 이벤트 처리(Process) 정보 조회
        NoiseEventProcess process = noiseEventDAO.findNoiseEventDetail(noiseEventId);
        return toDetailResponse(process);
    }
    // 즉시처리필요/승인대기 이벤트 목록 조회
    @Override
    public Page<NoiseEventProcess> findUrgentNoiseByStatus(Pageable pageable) {
        return noiseEventProcessRepository
                .findByUrgentBreakTrueAndStatus(ProcessStatus.UNPROCESSED, pageable);
    }
    // 소음 이벤트 승인 처리 (이벤트 상태를 APPROVED로 변경 >> 관리자 메모 저장)
    // 알림 발송 완료
    @Override
    public void notifyNoiseEvent(Long noiseEventId, String adminMemo) {
        NoiseEventProcess process = noiseEventDAO.findNoiseEventDetail(noiseEventId);
        process.setStatus(ProcessStatus.NOTIFIED);
        process.setAdminMemo(adminMemo);
    }
    // 소음 이벤트 보류 처리 (이벤트 상태를 HOLD로 변경 >> 관리자 메모 저장)
    // 관찰 시작 (미처리 -> 관찰 중)
    @Override
    public void startObserving(Long noiseEventId, String adminMemo) {
        NoiseEventProcess process = noiseEventDAO.findNoiseEventDetail(noiseEventId);
        process.setStatus(ProcessStatus.OBSERVING);
        process.setAdminMemo(adminMemo);
    }

    // 목록 조회용 DTO
    private NoiseEventListResponse toListResponse(NoiseEventProcess process) {
        NoiseEvent noiseEvent = process.getNoiseEvent();
        NoiseEventAnalysis analysis = noiseEvent.getNoiseEventAnalysis();
        NoiseSensor sensor = noiseEvent.getNoiseSensor();

        House upper = sensor.getUpperHouse();
        House lower = sensor.getLowerHouse();

        return NoiseEventListResponse.builder()
                .noiseEventId(noiseEvent.getNoiseEventId())
                // 발생 구간 (센서 기준)
                .upperHouseDong(upper.getHouseDong())
                .upperHouseHo(upper.getHouseHo())
                .lowerHouseDong(lower.getHouseDong())
                .lowerHouseHo(lower.getHouseHo())
                .occurredAt(noiseEvent.getCreatedAt())
                .timeZone(
                        noisePolicyService.isDayTime(
                                noiseEvent.getCreatedAt().toLocalTime()
                        ) ? "주간" : "야간"
                )
                .soundLevel(noiseEvent.getSoundLevel())
                .noisePattern1(analysis.getNoisePattern1())
                .repeatCount(analysis.getRepeatCount())
                .status(process.getStatus())
                .urgentBreak(process.getUrgentBreak())
                .recurrent(isRecurred(process))
                .habitual(isHabitual(process))
                .build();
    }

    // 즉시 처리 필요 목록 DTO
    private NoiseUrgentEventResponse toUrgentResponse(NoiseEventProcess process) {
        NoiseEvent noiseEvent = process.getNoiseEvent();
        NoiseEventAnalysis analysis = noiseEvent.getNoiseEventAnalysis();
        NoiseSensor sensor = noiseEvent.getNoiseSensor();

        House upper = sensor.getUpperHouse();
        House lower = sensor.getLowerHouse();

        return NoiseUrgentEventResponse.builder()
                .noiseEventId(noiseEvent.getNoiseEventId())
                .upperHouseDong(upper.getHouseDong())
                .upperHouseHo(upper.getHouseHo())
                .lowerHouseDong(lower.getHouseDong())
                .lowerHouseHo(lower.getHouseHo())
                .occurredAt(noiseEvent.getCreatedAt())
                .timeZone(
                        noisePolicyService.isDayTime(
                                noiseEvent.getCreatedAt().toLocalTime()
                        ) ? "주간" : "야간"
                )
                .soundLevel(noiseEvent.getSoundLevel())
                .noisePattern1(analysis.getNoisePattern1())
                .repeatCount(analysis.getRepeatCount())
                .status(process.getStatus())
                .build();
    }

    // 상세 조회용 DTO
    private NoiseEventDetailResponse toDetailResponse(NoiseEventProcess process) {
        NoiseEvent noiseEvent = process.getNoiseEvent();
        NoiseEventAnalysis analysis = noiseEvent.getNoiseEventAnalysis();
        NoiseSensor sensor = noiseEvent.getNoiseSensor();
        House upper = sensor.getUpperHouse();
        House lower = sensor.getLowerHouse();
        boolean habitual = isHabitual(process);
        boolean canRegisterHabitual = habitual && !existsHabitualZone(sensor);

        return NoiseEventDetailResponse.builder()
                .noiseEventId(noiseEvent.getNoiseEventId())
                .noiseEventProcessId(process.getProcessId())
                // 위치 정보
                .upperHouseDong(upper.getHouseDong())
                .upperHouseHo(upper.getHouseHo())
                .lowerHouseDong(lower.getHouseDong())
                .lowerHouseHo(lower.getHouseHo())
                // 발생 정보
                .occurredAt(noiseEvent.getCreatedAt())
                .soundLevel(noiseEvent.getSoundLevel())
                // 센서 정보
                .sensorType(sensor.getSensorType().name())
                // 시간대
                .timeZone(
                        noisePolicyService.isDayTime(
                                noiseEvent.getCreatedAt().toLocalTime()
                        ) ? "주간" : "야간"
                )
                // 소음 패턴
                .noisePattern1(analysis.getNoisePattern1())
                .noisePattern2(analysis.getNoisePattern2())
                // 반복 횟수
                .repeatCount(analysis.getRepeatCount())
                // 정책 위반 여부
                .policyBreak(analysis.getPolicyBreak())
                .analysisNote(analysis.getAnalysisNote())
                // 처리 상태
                .status(process.getStatus())
                .adminMemo(process.getAdminMemo())

                .habitual(habitual)
                .canRegisterHabitual(canRegisterHabitual)
                .build();
    }
    // 재발 여부 계산
    private boolean isRecurred(NoiseEventProcess process) {
        if (process.getStatus() != ProcessStatus.OBSERVING) {
            return false;
        }
        NoiseSensor sensor = process.getNoiseEvent().getNoiseSensor();
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        long count = noiseEventProcessRepository
                .countByNoiseEvent_NoiseSensorAndStatusAndCreatedAtAfter(sensor, ProcessStatus.OBSERVING, since);
        return count >= 3;
    }
    // 상습여부계산
    private boolean isHabitual(NoiseEventProcess process) {
        NoiseSensor sensor = process.getNoiseEvent().getNoiseSensor();
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        long count = noiseEventProcessRepository
                .countByNoiseEvent_NoiseSensorAndUrgentBreakTrueAndCreatedAtAfter(sensor, since);
        return count >= 5;
    }
    // 상습구간 기준충족(버튼 나타나는 여부) 계산
    private boolean existsHabitualZone(NoiseSensor sensor) {
        return noiseHabitualZoneRepository.existsBySensorAndStatus(sensor, "MONITORING");
    }
}
