package com.jjld.domain.noise.dto;

import com.jjld.domain.noise.entity.NoiseHabitualZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseHabitualZoneListResponse {
    private Long zoneId;

    // 위치
    private Integer upperHouseDong;
    private Integer upperHouseHo;
    private Integer lowerHouseDong;
    private Integer lowerHouseHo;

    // 최근 30일 발생 횟수 (NoiseEventProcess 기준)
    private Long eventCount30d;

    // 평균 강도 (추정 dB)
    private Double avgSoundLevel;

    // 상습 구간 등록일
    private LocalDateTime startedAt;

    // MONITORING / CLOSED
    private String status;

    /* ===== 변환 메서드 ===== */
    public static NoiseHabitualZoneListResponse from(
            NoiseHabitualZone zone,
            Long eventCount30d,
            Double avgSoundLevel
    ) {
        return NoiseHabitualZoneListResponse.builder()
                .zoneId(zone.getZoneId())
                .upperHouseDong(zone.getUpperHouse().getHouseDong())
                .upperHouseHo(zone.getUpperHouse().getHouseHo())
                .lowerHouseDong(zone.getLowerHouse().getHouseDong())
                .lowerHouseHo(zone.getLowerHouse().getHouseHo())
                .eventCount30d(eventCount30d)
                .avgSoundLevel(avgSoundLevel)
                .startedAt(zone.getStartedAt())
                .status(zone.getStatus())
                .build();
    }
}
