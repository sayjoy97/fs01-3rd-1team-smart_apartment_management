package com.jjld.domain.elevator.controller;

import com.jjld.domain.elevator.dto.AdvertisementReq;
import com.jjld.domain.elevator.dto.ElevatorDetailRes;
import com.jjld.domain.elevator.dto.ElevatorReq;
import com.jjld.domain.elevator.dto.ElevatorRes;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import com.jjld.domain.elevator.service.AdvertisementService;
import com.jjld.domain.elevator.service.ElevatorService;
import com.jjld.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elevator/api")
@RequiredArgsConstructor
public class ElevatorController {
    private final ElevatorService elevatorService;
    private final AdvertisementService advertisementService;

    // 엘리베이터 생성
    @PostMapping("/admin/{adminId}")
    public ResponseEntity<?> createElevator(
            @PathVariable Long adminId,
            @Valid @RequestBody ElevatorReq elevatorReq
    ) {
        elevatorService.createElevator(adminId, elevatorReq);
        return ResponseEntity.ok(ApiResponse.success("엘리베이터 생성을 성공했습니다."));
    }

    // 엘리베이터 목록 조회
    @GetMapping
    public ResponseEntity<?> getElevators() {
        List<ElevatorRes> response = elevatorService.getElevators();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 엘리베이터 상태 변경
    @PutMapping("/{elevatorId}")
    public ResponseEntity<?> updateElevatorState(
            @PathVariable Long elevatorId,
            @RequestParam ElevatorState elevatorState
    ) {
        elevatorService.updateElevatorState(elevatorId, elevatorState);
        return ResponseEntity.ok(ApiResponse.success("엘리베이터 상태 변경을 성공했습니다."));
    }

    // 엘리베이터 삭제
    @DeleteMapping("/{elevatorId}/admin/{adminId}")
    public ResponseEntity<?> deleteElevator(
            @PathVariable Long elevatorId,
            @PathVariable Long adminId
    ) {
        elevatorService.deleteElevator(elevatorId, adminId);
        return ResponseEntity.ok(ApiResponse.success("엘리베이터 삭제를 성공했습니다."));
    }

    // 엘리베이터 상세 조회
    @GetMapping("/{elevatorId}")
    public ResponseEntity<?> getElevatorDetailInfo(@PathVariable Long elevatorId) {
        ElevatorDetailRes response =  elevatorService.getElevatorDetailInfo(elevatorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 엘리베이터 광고 등록
    @PostMapping("/admin/{adminId}/advertisement")
    public ResponseEntity<?> createAdvertisement(
            @PathVariable Long adminId,
            @Valid @RequestBody AdvertisementReq advertisementReq
    ) {
        advertisementService.createAdvertisement(adminId, advertisementReq);
        return ResponseEntity.ok(ApiResponse.success("광고 등록을 성공했습니다."));
    }
}
