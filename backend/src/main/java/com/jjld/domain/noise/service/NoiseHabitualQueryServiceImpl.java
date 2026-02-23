package com.jjld.domain.noise.service;

import com.jjld.domain.noise.dto.NoiseHabitualZoneDetailResponse;
import com.jjld.domain.noise.dto.NoiseHabitualZoneListResponse;
import com.jjld.domain.noise.entity.NoiseEventProcess;
import com.jjld.domain.noise.entity.NoiseHabitualZone;
import com.jjld.domain.noise.entity.NoiseSensor;
import com.jjld.domain.noise.repository.NoiseEventProcessRepository;
import com.jjld.domain.noise.repository.NoiseHabitualZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoiseHabitualQueryServiceImpl implements NoiseHabitualQueryService {
    private final NoiseHabitualZoneRepository zoneRepository;
    private final NoiseEventProcessRepository noiseEventProcessRepository;

    @Override
    public Page<NoiseHabitualZoneListResponse> getZones(String status, Pageable pageable) {
        Page<NoiseHabitualZone> zones = (status == null)
                ? zoneRepository.findAll(pageable)
                : zoneRepository.findByStatus(status, pageable);

        return zones.map(zone -> {
            Long count = countEventsLast30Days(zone.getSensor());
            Double avg = calculateAvgSound(zone.getSensor());
            return NoiseHabitualZoneListResponse.from(zone, count, avg);
        });
    }

    @Override
    public NoiseHabitualZoneDetailResponse getZoneDetail(Long zoneId) {
        NoiseHabitualZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() ->
                        new IllegalArgumentException("상습 구간이 존재하지 않습니다.")
                );
        NoiseSensor sensor = zone.getSensor();
        // 최근 30일 이벤트 수
        Long eventCount30d = countEventsLast30Days(sensor);
        // 평균 강도
        Double avgSoundLevel = calculateAvgSound(sensor);
        // 최근 14일 일별 발생 추이
        List<NoiseHabitualZoneDetailResponse.DailyCount> dailyCounts =
                calculateDailyCounts(sensor);
        // 타임라인 (최근 이벤트 순)
        List<NoiseHabitualZoneDetailResponse.NoiseEventTimelineItem> timeline =
                getTimeline(sensor);
        return NoiseHabitualZoneDetailResponse.from(
                zone,
                eventCount30d,
                avgSoundLevel,
                dailyCounts,
                timeline
        );
    }

    @Override
    public long countMonitoringZones() {
        return zoneRepository.countByStatus("MONITORING");
    }

    @Override
    public long countClosedZones() {
        return zoneRepository.countByStatus("CLOSED");
    }

    // 최근 30일 정책 위반 이벤트 수
    private Long countEventsLast30Days(NoiseSensor sensor) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        return noiseEventProcessRepository
                .countByNoiseEvent_NoiseSensorAndUrgentBreakTrueAndCreatedAtAfter(
                        sensor, since
                );
    }
    // 최근 30일 평균 소음 강도
    private Double calculateAvgSound(NoiseSensor sensor) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);

        List<NoiseEventProcess> processes =
                noiseEventProcessRepository
                        .findByNoiseEvent_NoiseSensorAndUrgentBreakTrueAndCreatedAtAfter(
                                sensor, since
                        );

        return processes.stream()
                .mapToInt(p -> p.getNoiseEvent().getSoundLevel())
                .average()
                .orElse(0.0);
    }
    // 최근 14일 일별 발생 건수
    private List<NoiseHabitualZoneDetailResponse.DailyCount> calculateDailyCounts(NoiseSensor sensor) {
        LocalDateTime since = LocalDateTime.now().minusDays(14);

        List<NoiseEventProcess> processes =
                noiseEventProcessRepository
                        .findByNoiseEvent_NoiseSensorAndUrgentBreakTrueAndCreatedAtAfter(
                                sensor, since
                        );
        return processes.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> NoiseHabitualZoneDetailResponse.DailyCount.builder()
                        .date(e.getKey())
                        .count(e.getValue())
                        .build()
                )
                .sorted(Comparator.comparing(NoiseHabitualZoneDetailResponse.DailyCount::getDate))
                .toList();
    }

    // 타임라인 (최근순)
    private List<NoiseHabitualZoneDetailResponse.NoiseEventTimelineItem> getTimeline(NoiseSensor sensor) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);

        List<NoiseEventProcess> processes =
                noiseEventProcessRepository
                        .findByNoiseEvent_NoiseSensorAndUrgentBreakTrueAndCreatedAtAfter(
                                sensor, since
                        );
        return processes.stream()
                .sorted(Comparator.comparing(NoiseEventProcess::getCreatedAt).reversed())
                .map(p -> NoiseHabitualZoneDetailResponse.NoiseEventTimelineItem.builder()
                        .noiseEventId(p.getNoiseEvent().getNoiseEventId())
                        .soundLevel(p.getNoiseEvent().getSoundLevel())
                        .occurredAt(p.getCreatedAt())
                        .urgentBreak(p.getUrgentBreak())
                        .build()
                )
                .toList();
    }
}