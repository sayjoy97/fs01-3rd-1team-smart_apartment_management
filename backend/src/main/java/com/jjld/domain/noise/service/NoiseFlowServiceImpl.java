package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseEventAnalysis;
import com.jjld.domain.noise.entity.NoiseEventProcess;
import com.jjld.domain.noise.repository.NoiseEventAnalysisRepository;
import com.jjld.domain.noise.repository.NoiseEventProcessRepository;
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
}
