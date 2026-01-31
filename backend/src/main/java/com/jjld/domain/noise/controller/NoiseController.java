package com.jjld.domain.noise.controller;

import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseSensor;
import com.jjld.domain.noise.repository.NoiseEventRepository;
import com.jjld.domain.noise.repository.NoiseSensorRepository;
import com.jjld.domain.noise.service.NoiseFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "noise-controller", description = "층간소음 이벤트 관리 API")
@RestController
@RequestMapping("/noise/api")
@RequiredArgsConstructor
public class NoiseController {
    private final NoiseFlowService noiseFlowService;
    private final NoiseEventRepository noiseEventRepository;
    private final NoiseSensorRepository noiseSensorRepository;

    // 소음 이벤트 수신 API - 센서에서 소음발생시 호출
    @PostMapping("/event")
    @Operation(summary = "층간소음 이벤트 등록", description = "센서에서 감지된 소음 이벤트를 등록한다.")
    public ResponseEntity<String> receiveNoiseEvent(
            @RequestParam Long sensorId,
            @RequestParam int soundLevel) {
        // 1. 센서조회
        NoiseSensor noiseSensor = noiseSensorRepository.findById(sensorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 센서(" + sensorId + ")입니다."));
        // 2. 소음이벤트 생성
        NoiseEvent noiseEvent = NoiseEvent.builder()
                .noiseSensor(noiseSensor)
                .soundLevel(soundLevel).build();
        // 3.  이벤트저장
        noiseEventRepository.save(noiseEvent);
        // 4. 소음이벤트 처리흐름 호출
        noiseFlowService.handleNoiseEvent(noiseEvent);
        return ResponseEntity.ok("소음 이벤트가 정상적으로 처리되었습니다.");
    }
}
