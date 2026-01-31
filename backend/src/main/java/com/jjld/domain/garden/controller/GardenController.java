package com.jjld.domain.garden.controller;

import com.jjld.domain.garden.dto.GardenReq;
import com.jjld.domain.garden.dto.GardenRes;
import com.jjld.domain.garden.dto.ScheduleReq;
import com.jjld.domain.garden.service.GardenService;
import com.jjld.domain.garden.service.ScheduleService;
import com.jjld.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garden/api")
@RequiredArgsConstructor
public class GardenController {
    private final GardenService gardenService;
    private final ScheduleService scheduleService;

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
}
