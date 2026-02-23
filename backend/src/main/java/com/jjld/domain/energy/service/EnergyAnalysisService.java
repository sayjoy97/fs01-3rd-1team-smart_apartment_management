package com.jjld.domain.energy.service;

public interface EnergyAnalysisService {
    // 특정 설비에 대해 최신 UsageSummary 기준으로 분석 수행
    void analyzeDevice(Long deviceId);
}
