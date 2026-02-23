package com.jjld.domain.noise.dto;

import com.jjld.domain.noise.entity.NoiseHabitualZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseHabitualZoneDetailResponse {
    private Long zoneId;

    // 위치
    private Integer upperHouseDong;
    private Integer upperHouseHo;
    private Integer lowerHouseDong;
    private Integer lowerHouseHo;

    // 상태
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    /* ===== 요약 ===== */
    private Long eventCount30d;
    private Double avgSoundLevel;

    /* ===== 최근 14일 발생 추이 ===== */
    private List<DailyCount> dailyCounts;

    /* ===== 발생 이력 타임라인 ===== */
    private List<NoiseEventTimelineItem> timeline;

    public static NoiseHabitualZoneDetailResponse from(
            NoiseHabitualZone zone,
            Long eventCount30d,
            Double avgSoundLevel,
            List<DailyCount> dailyCounts,
            List<NoiseEventTimelineItem> timeline
    ) {
        return NoiseHabitualZoneDetailResponse.builder()
                .zoneId(zone.getZoneId())
                .upperHouseDong(zone.getUpperHouse().getHouseDong())
                .upperHouseHo(zone.getUpperHouse().getHouseHo())
                .lowerHouseDong(zone.getLowerHouse().getHouseDong())
                .lowerHouseHo(zone.getLowerHouse().getHouseHo())
                .status(zone.getStatus())
                .startedAt(zone.getStartedAt())
                .endedAt(zone.getEndedAt())
                .eventCount30d(eventCount30d)
                .avgSoundLevel(avgSoundLevel)
                .dailyCounts(dailyCounts)
                .timeline(timeline)
                .build();
    }

    /* ===== 내부 DTO ===== */

    @Data
    @Builder
    public static class DailyCount {
        private LocalDate date;
        private Long count;
    }

    @Data
    @Builder
    public static class NoiseEventTimelineItem {
        private Long noiseEventId;
        private Integer soundLevel;
        private LocalDateTime occurredAt;
        private Boolean urgentBreak;
    }
}