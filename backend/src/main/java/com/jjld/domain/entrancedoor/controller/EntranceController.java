package com.jjld.domain.entrancedoor.controller;

import com.jjld.domain.entrancedoor.dto.DoorStatusRequest;
import com.jjld.domain.entrancedoor.dto.EntranceGateLogResponse;
import com.jjld.domain.entrancedoor.dto.EntranceGateLogSearchCond;
import com.jjld.domain.entrancedoor.dto.EntranceGateResponse;
import com.jjld.domain.entrancedoor.service.EntranceDoorService;
import com.jjld.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/entrance/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EntranceController {
    private final EntranceDoorService service;

    // 세대 동 조회
    @GetMapping("/dong/list")
    @Operation(summary = "세대별 동 현관 상태 조회")
    public ResponseEntity<?> getDongList(){
        List<EntranceGateResponse> dongList = service.findAll();

        return ResponseEntity.ok(
                ApiResponse.success(dongList)
        );
    }

    // 세대 동 상태 변경
    @PatchMapping("/doors/{doorId}/status")
    public ResponseEntity<?> updateDoorStatus(
            @PathVariable Long doorId,
            @RequestBody DoorStatusRequest request
            ){
        String status = request.getStatus();

        service.updateDoorStatus(doorId, status);

        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK)
        );
    }

    // 공동현관 출입 로그 페이징
    @GetMapping("/log")
    @Operation(summary = "공동현관 출입 로그 페이징")
    public Map<String, Object> search(
            EntranceGateLogSearchCond cond,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<EntranceGateLogResponse> result = service.search(cond, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("totalPages", result.getTotalPages());
        response.put("totalElements", result.getTotalElements());
        response.put("page", result.getNumber());
        return response;
    }
}
