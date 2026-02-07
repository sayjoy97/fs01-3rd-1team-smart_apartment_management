package com.jjld.domain.elevator.controller;

import com.jjld.domain.elevator.dto.ElevatorReq;
import com.jjld.domain.elevator.dto.ElevatorRes;
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

    // 엘리베이터 생성
    @PostMapping("/admin/{adminId}")
    public ResponseEntity<?> createElevator(
            @PathVariable Long adminId,
            @Valid @RequestBody ElevatorReq elevatorReq
    ) {
        elevatorService.createElevator(adminId, elevatorReq);
        return ResponseEntity.ok(ApiResponse.success("엘리베이터  성공했습니다."));
    }

    // 엘리베이터 목록 조회
    @GetMapping
    public ResponseEntity<?> getElevators() {
        List<ElevatorRes> response = elevatorService.getElevators();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
