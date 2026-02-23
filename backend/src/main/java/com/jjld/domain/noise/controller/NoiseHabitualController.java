package com.jjld.domain.noise.controller;

import com.jjld.domain.noise.dto.NoiseHabitualZoneDetailResponse;
import com.jjld.domain.noise.dto.NoiseHabitualZoneListResponse;
import com.jjld.domain.noise.service.NoiseHabitualQueryService;
import com.jjld.domain.noise.service.NoiseHabitualService;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import org.springframework.data.domain.Pageable;

@Tag(name = "noise-habitual-controller", description = "층간소음 상습 구간 관리 API")
@RestController
@RequestMapping("/noise/api/habitual")
@RequiredArgsConstructor
public class NoiseHabitualController {
    private final NoiseHabitualService habitualService;
    private final NoiseHabitualQueryService habitualQueryService;

    // 상습 구간 수동 등록
    // 시큐리티 적용하고 나서 adminId 제거(토큰에서 가져와 세션 기반 관리자 로그인으로)
    @PostMapping("/zones/register")
    @Operation(summary = "상습 구간 수동 등록", description = "소음 이벤트 처리(Process)를 기준으로 상습 구간을 수동 등록한다.")
    public ResponseEntity<?> registerHabitualZone(
            @RequestParam Long noiseEventProcessId,
            @RequestParam(required = false) String memo,
            Authentication authentication) {
        String adminLoginId = authentication.getName();
        habitualService.registerZone(noiseEventProcessId, adminLoginId, memo);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 상습 구간 모니터링 종료
    @PostMapping("/zones/{zoneId}/close")
    @Operation(summary = "상습 구간 모니터링 종료", description = "상습 구간을 종료(CLOSED) 처리한다.")
    public ResponseEntity<?> closeHabitualZone(
            @PathVariable Long zoneId,
            @RequestParam(required = false) String memo,
            Authentication authentication) {
        String adminLoginId = authentication.getName();
        habitualService.closeZone(zoneId, adminLoginId, memo);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 상습 구간 목록 조회
    @GetMapping("/zones")
    @Operation(summary = "상습 구간 목록 조회", description = "상습 구간 목록을 조회한다. status 파라미터로 MONITORING / CLOSED 필터 가능")
    public ResponseEntity<?> getHabitualZones(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<NoiseHabitualZoneListResponse> result =
                habitualQueryService.getZones(status, pageable);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 상습 구간 상세 조회 (모달용)
    @GetMapping("/zones/{zoneId}")
    @Operation(summary = "상습 구간 상세 조회", description = "상습 구간 상세 정보 + 최근 발생 추이 + 타임라인을 조회한다.")
    public ResponseEntity<?> getHabitualZoneDetail(
            @PathVariable Long zoneId) {
        NoiseHabitualZoneDetailResponse response =
                habitualQueryService.getZoneDetail(zoneId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
    // 상단 카드용 카운트
    @GetMapping("/zones/count")
    @Operation(summary = "상습 구간 상태별 개수", description = "모니터링 중 / 종료된 상습 구간 개수를 조회한다.")
    public ResponseEntity<?> getHabitualZoneCounts() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        new HabitualZoneCountResponse(
                                habitualQueryService.countMonitoringZones(),
                                habitualQueryService.countClosedZones()
                        )
                )
        );
    }

//    @PostMapping("/zones/{zoneId}/notify")
//    public ResponseEntity<?> notifyHabitualZone(
//            @PathVariable Long zoneId,
//            @RequestParam(required = false) String memo,
//            Authentication authentication
//    ) {
//        String adminLoginId = authentication.getName();
//        habitualService.notify(zoneId, adminLoginId, memo);
//        return ResponseEntity.ok(ApiResponse.success());
//    }
    // 내부 DTO (카운트용)
    private record HabitualZoneCountResponse(long monitoringCount, long closedCount) {
    }
}
