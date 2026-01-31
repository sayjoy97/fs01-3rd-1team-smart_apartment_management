package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.NoiseEvent;

// 소음이벤트 흐름 제어 >> 시작점
public interface NoiseFlowService {
    // 소음 이벤트 발생 처리
    // 반복횟수 계산 >> 소음위반 판단 Service 호출 >> 분석결과저장 >> 처리(Process)객체 생성
    void handleNoiseEvent(NoiseEvent noiseEvent);
}
