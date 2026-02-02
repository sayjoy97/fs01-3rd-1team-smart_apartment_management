package com.jjld.domain.noise.controller;

import com.jjld.domain.noise.dto.NoiseDashboardResponse;
import com.jjld.domain.noise.service.NoiseDashboardService;
import com.jjld.domain.noise.service.NoiseFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "noise-controller", description = "층간소음 이벤트 관리 API")
@RestController
@RequestMapping("/noise/api")
@RequiredArgsConstructor
public class NoiseController {
    private final NoiseFlowService noiseFlowService;
    private final NoiseDashboardService noiseDashboardService;

    // 소음 이벤트 수신 API - 센서에서 소음발생시 호출
    @PostMapping("/event")
    @Operation(summary = "층간소음 이벤트 등록", description = "센서에서 감지된 소음 이벤트를 등록한다.")
    public ResponseEntity<String> receiveNoiseEvent(
            @RequestParam Long sensorId,
            @RequestParam int soundLevel) {
        noiseFlowService.receiveNoiseEvent(sensorId, soundLevel);
        return ResponseEntity.ok("소음 이벤트가 정상적으로 처리되었습니다.");
    }

    @GetMapping("/dashboard")
    @Operation(
            summary = "대시보드 상단 요약 카드 조회",
            description = "오늘 발생 이벤트 수, 정책 위반 의심 수, 승인 대기 수, 현재 시간대(주/야)를 조회한다."
    )
    public ResponseEntity<NoiseDashboardResponse> getDashboardSummary() {
        return ResponseEntity.ok(noiseDashboardService.getDashboard());
    }
}
