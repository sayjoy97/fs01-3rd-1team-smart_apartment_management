package com.jjld.domain.noise.service;

import com.jjld.domain.noise.dto.NoiseDashboardResponse;

public interface NoiseDashboardService {
    // 대시보드 상단 요약 카드 조회
    NoiseDashboardResponse getDashboard();
}
