package com.jjld.domain.noise.dao;

import com.jjld.domain.noise.entity.Enum.NoisePattern1;
import com.jjld.domain.noise.entity.Enum.SensorType;
import com.jjld.domain.noise.entity.NoiseEventAnalysis;
import com.jjld.domain.noise.repository.NoiseEventAnalysisRepository;
import com.jjld.domain.noise.repository.NoiseEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NoiseStatisticsDAOImpl implements NoiseStatisticsDAO {
    private final NoiseEventRepository noiseEventRepository;
    private final NoiseEventAnalysisRepository noiseEventAnalysisRepository;
    // 시간대별 소음 발생 건수
    @Override
    public Map<Integer, Long> countNoiseEventByHour(LocalDate date) {
        // 하루 시작/끝 시간 계산
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return noiseEventRepository
                .findByCreatedAtBetween(start, end, null) // 전체 조회
                .stream()
                // createdAt에서 시간(hour)만 추출
                .collect(Collectors.groupingBy(
                        event -> event.getCreatedAt().getHour(),
                        Collectors.counting()
                ));
    }
    // 시간대별 정책 위반 건수
    @Override
    public Map<Integer, Long> countPolicyBreakByHour(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return noiseEventAnalysisRepository
                .findAll()
                .stream()
                // 날짜 필터
                .filter(analysis ->
                        !analysis.getCreatedAt().isBefore(start)
                                && analysis.getCreatedAt().isBefore(end)
                                && analysis.getPolicyBreak()
                )
                .collect(Collectors.groupingBy(
                        analysis -> analysis.getCreatedAt().getHour(),
                        Collectors.counting()
                ));
    }
    // 센서 신호 유형 분포(원그래프
    @Override
    public Map<SensorType, Long> countSensorType(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return noiseEventRepository
                .findByCreatedAtBetween(start, end, null)
                .stream()
                .collect(Collectors.groupingBy(
                        event -> event.getNoiseSensor().getSensorType(),
                        Collectors.counting()
                ));
    }
    // 소음 패턴 발생빈도(레이더차트
    @Override
    public Map<NoisePattern1, Long> countNoisePattern(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return noiseEventAnalysisRepository
                .findAll()
                .stream()
                // 날짜 필터
                .filter(analysis ->
                        !analysis.getCreatedAt().isBefore(start)
                                && analysis.getCreatedAt().isBefore(end)
                )
                .collect(Collectors.groupingBy(
                        NoiseEventAnalysis::getNoisePattern1,
                        Collectors.counting()
                ));
    }
}
