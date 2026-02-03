package com.jjld.domain.garden.controller;

import com.jjld.domain.garden.dto.*;
import com.jjld.domain.garden.entity.Enum.DeviceState;
import com.jjld.domain.garden.service.DeviceService;
import com.jjld.domain.garden.service.GardenService;
import com.jjld.domain.garden.service.ScheduleService;
import com.jjld.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garden/api")
@RequiredArgsConstructor
public class GardenController {
    private final GardenService gardenService;
    private final ScheduleService scheduleService;
    private final DeviceService deviceService;

    // 정원 관리 구역 생성
    @PostMapping
    public ResponseEntity<?> createGarden(@Valid @RequestBody GardenReq gardenReq) {
        gardenService.createGarden(gardenReq);
        return ResponseEntity.ok(ApiResponse.success("구역 추가를 성공했습니다."));
    }

    // 정원 관리 구역 목록 조회
    @GetMapping
    public ResponseEntity<?> getGardens() {
        List<GardenRes> response = gardenService.getGardens();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 정원 관리 구역 수정
    @PutMapping("/{gardenId}")
    public ResponseEntity<?> updateGarden(
            @PathVariable Long gardenId,
            @Valid @RequestBody GardenReq gardenReq
    ) {
        gardenService.updateGarden(gardenId, gardenReq);
        return ResponseEntity.ok(ApiResponse.success("구역 수정을 성공했습니다."));
    }

    // 정원 관리 구역 삭제
    @DeleteMapping("/{gardenId}/admin/{adminId}")
    public ResponseEntity<?> deleteGarden(
            @PathVariable Long gardenId,
            @PathVariable Long adminId
    ) {
        gardenService.deleteGarden(gardenId, adminId);
        return ResponseEntity.ok(ApiResponse.success("구역 삭제를 성공했습니다."));
    }

    // 정원 관리 일정 생성
    @PostMapping("/{gardenId}/schedule")
    public ResponseEntity<?> createSchedule(
            @PathVariable Long gardenId,
            @Valid @RequestBody ScheduleReq scheduleReq
    ) {
        scheduleService.createSchedule(gardenId, scheduleReq);
        return ResponseEntity.ok(ApiResponse.success("일정 생성을 성공했습니다."));
    }

    // 정원 관리 일정 필터 목록 조회
    @GetMapping("/schedule/filter")
    public ResponseEntity<?> getSchedules(ScheduleSearchCondition cond, Pageable pageable) {
        Page<ScheduleFilterRes> response = scheduleService.getSchedules(cond, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 정원 관리 일정 조회
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<?> getSchedule(@PathVariable Long scheduleId) {
        ScheduleRes response = scheduleService.getSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 정원 관리 일정 수정
    @PutMapping("/schedule/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateScheduleReq updateScheduleReq
    ) {
        scheduleService.updateSchedule(scheduleId, updateScheduleReq);
        return ResponseEntity.ok(ApiResponse.success("일정 수정을 성공했습니다."));
    }

    // 정원 관리 일정 삭제
    @DeleteMapping("/schedule/{scheduleId}/admin/{adminId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long scheduleId,
            @PathVariable Long adminId
    ) {
        scheduleService.deleteSchedule(scheduleId, adminId);
        return ResponseEntity.ok(ApiResponse.success("일정 삭제를 성공했습니다."));
    }

    // 정원 관리 기능 자동 급수 토글 버튼
    @PutMapping("/{gardenId}/toggle-auto-watering")
    public ResponseEntity<?> toggleWatering(@PathVariable Long gardenId) {
        gardenService.toggleWatering(gardenId);
        return ResponseEntity.ok(ApiResponse.success("자동 급수 상태 변경을 성공했습니다."));
    }

    // 정원 관리 기능 디바이스 등록
    @PostMapping("/{gardenId}/device")
    public ResponseEntity<?> createDevices(
            @PathVariable Long gardenId,
            @Valid @RequestBody List<DeviceReq> deviceReqs
    ) {
        deviceService.createDevices(gardenId, deviceReqs);
        return ResponseEntity.ok(ApiResponse.success("디바이스 등록을 성공했습니다."));
    }

    // 정원 관리 기능 디바이스 상태 수정
    @PutMapping("/device/{deviceId}")
    public ResponseEntity<?> updateDevice(
            @PathVariable Long deviceId,
            @RequestParam DeviceState deviceState
            ) {
        deviceService.updateDevice(deviceId, deviceState);
        return ResponseEntity.ok(ApiResponse.success("디바이스 수정을 성공했습니다."));
    }

    // 정원 관리 기능 수동 물주기
    @PostMapping("/{gardenId}/manual-watering")
    public ResponseEntity<?> manualWatering(@PathVariable Long gardenId) {
        deviceService.manualWatering(gardenId);
        return ResponseEntity.ok(ApiResponse.success("수동 물주기를 성공했습니다."));
    }
}
