package com.jjld.domain.noise.controller;

import com.jjld.domain.noise.dto.NoiseEventDecisionRequest;
import com.jjld.domain.noise.dto.NoisePolicyCreateRequest;
import com.jjld.domain.noise.dto.NoisePolicyResponse;
import com.jjld.domain.noise.dto.NoiseUrgentEventResponse;
import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.NoisePolicy;
import com.jjld.domain.noise.service.*;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "noise-controller", description = "층간소음 이벤트 관리 API")
@RestController
@RequestMapping("/noise/api")
@RequiredArgsConstructor
public class NoiseController {
    private final NoiseFlowService noiseFlowService;
    private final NoiseDashboardService noiseDashboardService;
    private final NoiseEventService noiseEventService;
    private final NoiseStatisticsService noiseStatisticsService;
    private final NoisePolicyService noisePolicyService;


    // 소음 이벤트 수신 API - 센서에서 소음발생시 호출
    @PostMapping("/event/add")
    @Operation(summary = "층간소음 이벤트 등록", description = "센서에서 감지된 소음 이벤트를 등록한다.")
    public ResponseEntity<?> receiveNoiseEvent(
            @RequestParam Long sensorId,
            @RequestParam int soundLevel) {
        noiseFlowService.receiveNoiseEvent(sensorId, soundLevel);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/dashboard")
    @Operation(
            summary = "대시보드 상단 요약 카드 조회",
            description = "오늘 발생 이벤트 수, 정책 위반 의심 수, 긴급 이벤트 수, 현재 시간대(주/야)를 조회한다."
    )
    public ResponseEntity<?> getDashboardSummary() {
        return ResponseEntity.ok(ApiResponse.success(noiseDashboardService.getDashboard()));
    }

    @GetMapping("/urgent")
    @Operation(summary = "즉시 처리 필요 소음 이벤트 목록 조회", description = "정책 위반 의심 + 승인 대기 상태의 소음 이벤트 목록을 조회한다.")
    public ResponseEntity<?> getUrgentNoiseEvents(@PageableDefault(size = 10) Pageable pageable) {
        Page<NoiseUrgentEventResponse> result = noiseEventService.getUrgentNoiseEventResponses(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    // 이벤트 상세 조회 API
    @GetMapping("/event/{noiseEventId}")
    @Operation(summary = "소음 이벤트 상세 조회", description = "선택한 소음 이벤트의 상세 정보(분석 결과, 상태, 관리자 메모 등)를 조회한다.")
    public ResponseEntity<?> getNoiseEventDetail(@PathVariable Long noiseEventId) {
        return ResponseEntity.ok(ApiResponse.success(noiseEventService.getNoiseEventDetailResponse(noiseEventId)));
    }

    //관찰 시작 (상태 변경)
    @PostMapping("/event/{noiseEventId}/observe")
    @Operation(summary = "소음 이벤트 관찰 시작", description = "소음 이벤트를 관찰 중 상태로 변경한다.")
    public ResponseEntity<?> observeNoiseEvent(@PathVariable Long noiseEventId, @RequestBody NoiseEventDecisionRequest request
    ) {
        noiseEventService.startObserving(noiseEventId, request.getAdminMemo());
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 알림 발송 처리
    @PostMapping("/event/{noiseEventId}/notify")
    @Operation(summary = "소음 이벤트 알림 발송", description = "입주민 또는 관리자 대상 알림을 발송하고 상태를 NOTIFIED로 변경한다.")
    public ResponseEntity<?> notifyNoiseEvent(@PathVariable Long noiseEventId, @RequestBody NoiseEventDecisionRequest request) {
        noiseEventService.notifyNoiseEvent(noiseEventId, request.getAdminMemo());
        return ResponseEntity.ok(ApiResponse.success());
    }
    // 이벤트 통계 API
    @GetMapping("/statistics")
    @Operation(summary = "소음 통계/그래프 데이터 조회", description = "시간대별 소음 발생, 정책 위반, 센서 유형 분포, 소음 패턴 통계를 조회한다.")
    public ResponseEntity<?> getNoiseStatistics(@RequestParam(required = false) LocalDate date) {
        // date가 없으면 서비스에서 오늘 기준 처리
        return ResponseEntity.ok(ApiResponse.success(noiseStatisticsService.getStatistics(date)));
    }
    @GetMapping("/events")
    @Operation(summary = "소음 이벤트 목록 조회", description = "status(선택) + viewMode(all/day/night)(선택)로 목록을 조회한다.")
    public ResponseEntity<?> getNoiseEvents(
            @RequestParam(required = false) ProcessStatus status,
            @RequestParam(required = false, defaultValue = "all") String viewMode,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        noiseEventService.getNoiseEventListResponses(status, viewMode, pageable)
                )
        );
    }

    @GetMapping("/policy/active")
    @Operation(summary = "현재 소음 정책 조회", description = "")
    public ResponseEntity<?> getActivePolicy() {
        NoisePolicy p = noisePolicyService.findActiveNoisePolicy();
        NoisePolicyResponse res = NoisePolicyResponse.builder()
                .policyId(p.getPolicyId())
                .policyName(p.getPolicyName())
                .dayStartTime(p.getDayStartTime())
                .nightStartTime(p.getNightStartTime())
                .soundLimit(p.getSoundLimit())
                .repeatLimit(p.getRepeatLimit())
                .timeThreshold(p.getTimeThreshold())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .build();
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping("/policy")
    @Operation(summary = "새로운 소음 정책 생성", description = "")
    public ResponseEntity<?> createPolicy(@RequestBody NoisePolicyCreateRequest req) {
        NoisePolicy policy = NoisePolicy.builder()
                .policyName(req.getPolicyName())
                .dayStartTime(req.getDayStartTime())
                .nightStartTime(req.getNightStartTime())
                .soundLimit(req.getSoundLimit())
                .repeatLimit(req.getRepeatLimit())
                .timeThreshold(req.getTimeThreshold())
                .build();

        noisePolicyService.createNoisePolicy(policy);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
