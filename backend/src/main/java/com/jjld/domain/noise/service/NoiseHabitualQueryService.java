package com.jjld.domain.noise.service;

import com.jjld.domain.noise.dto.NoiseHabitualZoneDetailResponse;
import com.jjld.domain.noise.dto.NoiseHabitualZoneListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoiseHabitualQueryService {
    // 상습 구간 목록
    Page<NoiseHabitualZoneListResponse> getZones(String status, Pageable pageable);

    // 상습 구간 상세 (모달)
    NoiseHabitualZoneDetailResponse getZoneDetail(Long zoneId);

    // 상단 카드용
    long countMonitoringZones();
    long countClosedZones();
}
