package com.jjld.domain.energy.controller;

import com.jjld.domain.energy.dto.*;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import com.jjld.domain.energy.service.*;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "energy-controller", description = "에너지 관리 API")
@RestController
@RequestMapping("/energy/api")
@RequiredArgsConstructor
public class EnergyController {
    private final EnergyDashboardService energyDashboardService;
    private final EnergyPolicyService energyPolicyService;
    private final EnergyDeviceService energyDeviceService;
    private final EnergyUsageSummaryService energyUsageSummaryService;
    private final EnergyMeasurementService energyMeasurementService;
    private final EnergyChartService energyChartService;

    // 1) 소비패턴

    // 월간사용량.오늘점검필요건수.점검대기설비수
    @GetMapping("/dashboard")
    @Operation(summary = "에너지 대시보드 상단 요약 카드 조회", description = "이번 달 총 사용량, 오늘 점검 필요 건수, 점검 대기 설비 수를 조회한다.")
    public ResponseEntity<?> getEnergyDashboard() {
        return ResponseEntity.ok(ApiResponse.success(energyDashboardService.getDashboard()));
    }
    // 정책 API
    @GetMapping("/policy/active")
    @Operation(summary = "활성 에너지 정책 조회", description = "현재 활성화된 에너지 정책 1개를 조회한다.")
    public ResponseEntity<?> getActivePolicy() {
        return ResponseEntity.ok(ApiResponse.success(energyPolicyService.getActivePolicy()));
    }
    @GetMapping("/policy/history")
    @Operation(summary = "에너지 정책 이력 조회", description = "등록된 에너지 정책 이력을 페이지 단위로 조회한다.")
    public ResponseEntity<?> getPolicyHistory(@PageableDefault(size = 10) Pageable pageable) {
        Page<?> result = energyPolicyService.getPolicyHistory(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    @PostMapping("/policy")
    @Operation(summary = "에너지 정책 생성", description = "기존 활성 정책을 비활성화하고 새 정책을 활성화 상태로 생성한다.")
    public ResponseEntity<?> createPolicy(@RequestBody EnergyPolicyCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(energyPolicyService.createPolicy(request)));
    }
    // 에너지 설비 관련 API
    @GetMapping("/devices")
    @Operation(summary = "설비 목록 조회", description = "설비 상태별 필터링 및 최신 분석 기준으로 목록을 조회한다.")
    public ResponseEntity<?> getDeviceList(
            @RequestParam(required = false) DeviceStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<?> result = energyDeviceService.getDeviceList(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    @GetMapping("/devices/check-required")
    @Operation(summary = "우선 점검 필요 설비 목록 조회", description = "설비별 최신 분석 결과 중 점검 권장 상태(CHECK_REQUIRED)인 설비 목록을 조회한다.")
    public ResponseEntity<?> getCheckRequiredDevices(@PageableDefault(size = 10) Pageable pageable) {
        Page<?> result = energyDeviceService.getCheckRequiredDevices(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    @PostMapping("/devices/{deviceId}/start-check")
    @Operation(summary = "설비 점검 시작", description = "관리자가 설비 점검을 시작하면 상태를 CHECKING으로 변경한다. 이미 CHECKING 상태인 경우 상태는 유지된다.")
    public ResponseEntity<?> startCheck(@PathVariable Long deviceId) {
        energyDeviceService.startCheck(deviceId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @PostMapping("/devices/{deviceId}/complete-check")
    @Operation(summary = "설비 점검 완료", description = "관리자가 설비 점검을 완료하면 상태를 NORMAL로 변경한다.")
    public ResponseEntity<?> completeCheck(@PathVariable Long deviceId) {
        energyDeviceService.completeCheck(deviceId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @GetMapping("/devices/{deviceId}")
    @Operation(summary = "설비 상세 조회", description = "설비 기본 정보와 최신 분석 정보를 조회한다.")
    public ResponseEntity<?> getDeviceDetail(@PathVariable Long deviceId) {
        return ResponseEntity.ok(ApiResponse.success(
                energyDeviceService.getDeviceDetail(deviceId)
        ));
    }
    @GetMapping("/devices/{deviceId}/control-logs")
    @Operation(summary = "설비 운영 이력 조회", description = "설비의 ON/OFF 제어 이력을 최신순으로 조회한다.")
    public ResponseEntity<?> getControlLogs(@PathVariable Long deviceId) {
        return ResponseEntity.ok(ApiResponse.success(
                energyDeviceService.getControlLogs(deviceId)
        ));
    }
    @PostMapping("/devices/{deviceId}/control")
    @Operation(summary = "설비 ON/OFF 제어", description = "설비 운영 상태를 변경하고 제어 이력을 저장한다.")
    public ResponseEntity<?> controlDevice(
            @PathVariable Long deviceId, @RequestParam Boolean operate, @RequestParam String reason) {
        energyDeviceService.controlDevice(deviceId, operate, reason);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @GetMapping("/devices/{deviceId}/saving-results")
    @Operation(summary = "설비 절감 효과 이력 조회")
    public ResponseEntity<?> getSavingResults(
            @PathVariable Long deviceId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(energyDeviceService.getSavingResults(deviceId, pageable))
        );
    }

    @PostMapping("/usage")
    @Operation(summary = "에너지 사용량 요약 등록(수집 데이터 적재)", description = "설비별 실제/예상 사용량을 저장하고, 저장 직후 분석을 수행하여 설비 상태를 동기화한다.")
    public ResponseEntity<?> createUsageSummary(@RequestBody EnergyUsageSummaryCreateRequest request) {
        energyUsageSummaryService.createSummary(request);
        return ResponseEntity.ok(ApiResponse.success());
    }


    @PostMapping("/measurement")
    @Operation(summary = "실시간 측정 데이터 수집", description = "센서에서 수집된 전압/전류/전력/누적전력 데이터를 저장하고 자동 집계 및 분석을 수행한다.")
    public ResponseEntity<?> createMeasurement(
            @RequestBody EnergyMeasurementCreateRequest request) {

        energyMeasurementService.createMeasurement(request);
        return ResponseEntity.ok(ApiResponse.success());
    }


    // 1) 소비패턴
    @GetMapping("/pattern")
    public ApiResponse<List<PatternPointDTO>> pattern(
            @RequestParam String period, // TIME_SLOT | DAILY | MONTHLY
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baseDate
    ) {
        return ApiResponse.success(energyChartService.getPattern(period, deviceId, baseDate));
    }

    // 2) 유형분포(월간)
    @GetMapping("/category")
    public ApiResponse<List<CategorySliceDTO>> category(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month,
            @RequestParam(required = false) Long deviceId
    ) {
        return ApiResponse.success(energyChartService.getCategory(month, deviceId));
    }
}
