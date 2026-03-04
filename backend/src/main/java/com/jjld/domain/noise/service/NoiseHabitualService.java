package com.jjld.domain.noise.service;

public interface NoiseHabitualService {
    // 상습구간 수동 등록
    void registerZone(Long noiseEventProcessId, String adminLoginId, String memo);

    // 모니터링 종료
    void closeZone(Long zoneId, String adminLoginId, String memo);
}
