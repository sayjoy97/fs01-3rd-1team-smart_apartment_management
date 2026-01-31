package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseEventAnalysis;

public interface NoiseViolationService {
    // 소음 이벤트를 정책 기준으로 분석
    NoiseEventAnalysis analyzeNoiseEvent(
            NoiseEvent noiseEvent,
            int repeatCount
    );
}
