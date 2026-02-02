package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseEventAnalysis;
import com.jjld.domain.noise.entity.NoiseEventProcess;
import com.jjld.domain.noise.entity.NoiseSensor;
import com.jjld.domain.noise.repository.NoiseEventAnalysisRepository;
import com.jjld.domain.noise.repository.NoiseEventProcessRepository;
import com.jjld.domain.noise.repository.NoiseEventRepository;
import com.jjld.domain.noise.repository.NoiseSensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoiseFlowServiceImpl implements NoiseFlowService {
    private final NoiseViolationService noiseViolationService;
    private final NoiseEventAnalysisRepository noiseEventAnalysisRepository;
    private final NoiseEventProcessRepository noiseEventProcessRepository;
    private final NoiseSensorRepository noiseSensorRepository;
    private final NoiseEventRepository noiseEventRepository;

    // 소음 이벤트 처리 흐름
    @Override
    public void handleNoiseEvent(NoiseEvent noiseEvent) {
        // 1. 반복횟수계산 - 같은센서/정책 시간/발생이벤트 수 기준
        int repeatCount = 1;
        // 2. 정책 기준으로 소음 분석
        NoiseEventAnalysis analysis = noiseViolationService.analyzeNoiseEvent(noiseEvent, repeatCount);
        // 3. 분석결과저장
        noiseEventAnalysisRepository.save(analysis);
        // 4. Process 객체 생성
        NoiseEventProcess process = NoiseEventProcess.builder()
                .noiseEvent(noiseEvent)
                .status(ProcessStatus.PENDING)
                .urgentBreak(analysis.getPolicyBreak())
                .build();
        noiseEventProcessRepository.save(process);
    }
    @Override
    public void receiveNoiseEvent(Long sensorId, int soundLevel) {
        // 1. 센서 조회
        NoiseSensor noiseSensor = noiseSensorRepository.findById(sensorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 센서(" + sensorId + ")입니다.")
                );
        // 2. 소음 이벤트 생성
        NoiseEvent noiseEvent = NoiseEvent.builder()
                .noiseSensor(noiseSensor)
                .soundLevel(soundLevel)
                .build();
        // 3. 이벤트 저장
        noiseEventRepository.save(noiseEvent);
        // 4. 기존 처리 흐름
        handleNoiseEvent(noiseEvent);
    }
}
