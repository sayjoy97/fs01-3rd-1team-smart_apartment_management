package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.*;
import com.jjld.domain.noise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NoiseFlowServiceImpl implements NoiseFlowService {
    private final NoiseViolationService noiseViolationService;
    private final NoiseEventAnalysisRepository noiseEventAnalysisRepository;
    private final NoiseEventProcessRepository noiseEventProcessRepository;
    private final NoiseSensorRepository noiseSensorRepository;
    private final NoiseEventRepository noiseEventRepository;
    private final NoisePolicyRepository noisePolicyRepository;
    private final NoiseHabitualService noiseHabitualService;

    // 소음 이벤트 처리 흐름
    @Override
    public void handleNoiseEvent(NoiseEvent noiseEvent) {
        // 활성 정책 조회 (반드시 1개여야 함)
        NoisePolicy policy = noisePolicyRepository.findByIsActiveTrue()
                .orElseThrow(() -> new IllegalStateException("활성화된 소음 정책이 존재하지 않습니다."));
        // 1. 반복횟수계산 - 같은센서/정책 시간/발생이벤트 수 기준
        int repeatCount = calculateRepeatCount(noiseEvent, policy);
        // 2. 정책 기준으로 소음 분석
        NoiseEventAnalysis analysis = noiseViolationService.analyzeNoiseEvent(noiseEvent, repeatCount, policy);
        // NoiseEvent와 연결 (연관관계 주인 명시)
        analysis.setNoiseEvent(noiseEvent);
        // 3. 분석 결과 저장
        noiseEventAnalysisRepository.save(analysis);
        boolean urgentBreak = analysis.getPolicyBreak() && analysis.getAnalysisNote().contains("진동 감지");
        // 위 한 줄은 예시가 아니라, 아래처럼 정확히 분리하는 게 맞음 -> 다음 블록 참고
        // 6. Process 객체 생성
        NoiseEventProcess process = NoiseEventProcess.builder()
                .noiseEvent(noiseEvent)
                .noisePolicy(policy)
                .status(ProcessStatus.UNPROCESSED) // 항상 최초에는 미처리 상태로
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

    // repeatCount 계산 메서드
    private int calculateRepeatCount(NoiseEvent event, NoisePolicy policy) {
        int windowMin = policy.getTimeThreshold(); // 분
        LocalDateTime since = event.getCreatedAt().minusMinutes(windowMin);
        return (int) noiseEventRepository
                .countByNoiseSensorAndCreatedAtAfter(event.getNoiseSensor(), since);
    }
}
