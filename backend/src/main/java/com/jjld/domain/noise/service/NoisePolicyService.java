package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.NoisePolicy;

import java.time.LocalTime;

public interface NoisePolicyService {
    // 새로운 소음 정책 생성 및 적용
    // 기존 활성 정책 비활성 >> 신규 정책 저장 >> 신규 정책 활성
    void createNoisePolicy(NoisePolicy noisePolicy);

    // 현재 적용 중 소음 정책 조회
    NoisePolicy findActiveNoisePolicy();

    // 특정 시간이 주간인지 판단 (주간이면 true)
    boolean isDayTime(LocalTime time);

    // 현재 시간이 주간인지 판단
    boolean isDayTimeNow();
}
