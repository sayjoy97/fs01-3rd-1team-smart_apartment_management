package com.jjld.domain.entrancedoor.controller;

import com.jjld.domain.entrancedoor.dto.EntranceGateLogResponse;
import com.jjld.domain.entrancedoor.dto.EntranceGateResponse;
import com.jjld.domain.entrancedoor.service.EntranceDoorService;
import com.jjld.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entrance/api")
@RequiredArgsConstructor
public class EntranceController {
    private final EntranceDoorService service;

    // 세대 동 조회
    @GetMapping("/dong/list")
    public ResponseEntity<?> getDongList(){
        List<EntranceGateResponse> dongList = service.findAll();

        return ResponseEntity.ok(
                ApiResponse.success(dongList)
        );
    }

    // 공동현관 출입 로그 페이징
    @GetMapping("/log")
    public Page<EntranceGateLogResponse> getLogPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return service.findAll(page, size);
    }
}
