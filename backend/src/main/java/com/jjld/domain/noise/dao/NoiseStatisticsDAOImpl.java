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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NoiseStatisticsDAOImpl implements NoiseStatisticsDAO {
    private final NoiseEventRepository noiseEventRepository;
    private final NoiseEventAnalysisRepository noiseEventAnalysisRepository;
    // 시간대별 소음 발생 건수
//    @Override
//    public Map<Integer, Long> countNoiseEventByHour(LocalDateTime start, LocalDateTime end) {
//        return noiseEventRepository
//                .findByCreatedAtBetween(start, end) // 전체 조회
//                .stream()
//                // createdAt에서 시간(hour)만 추출
//                .collect(Collectors.groupingBy(
//                        event -> event.getCreatedAt().getHour(),
//                        Collectors.counting()
//                ));
//    }
//    // 시간대별 정책 위반 건수
//    @Override
//    public Map<Integer, Long> countPolicyBreakByHour(LocalDateTime start, LocalDateTime end) {
//        return noiseEventAnalysisRepository
//                .findByCreatedAtBetween(start, end)
//                .stream()
//                .filter(NoiseEventAnalysis::getPolicyBreak)
//                .collect(Collectors.groupingBy(
//                        analysis -> analysis.getCreatedAt().getHour(),
//                        Collectors.counting()
//                ));
//    }
//    // 센서 신호 유형 분포(원그래프
//    @Override
//    public Map<SensorType, Long> countSensorType(LocalDateTime start, LocalDateTime end) {
//        return noiseEventRepository
//                .findByCreatedAtBetween(start, end)
//                .stream()
//                .collect(Collectors.groupingBy(
//                        event -> event.getNoiseSensor().getSensorType(),
//                        Collectors.counting()
//                ));
//    }
//    // 소음 패턴 발생빈도(레이더차트
//    @Override
//    public Map<NoisePattern1, Long> countNoisePattern(LocalDateTime start, LocalDateTime end) {
//        return noiseEventAnalysisRepository
//                .findByCreatedAtBetween(start, end)
//                .stream()
//                .collect(Collectors.groupingBy(
//                        NoiseEventAnalysis::getNoisePattern1,
//                        Collectors.counting()
//                ));
//    }
//}
    @Override
    public Map<Integer, Long> countNoiseEventByHour(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rows = noiseEventRepository.countByHour(start, end);

        Map<Integer, Long> map = new LinkedHashMap<>();
        for (int h = 0; h < 24; h++) map.put(h, 0L); // 차트 안정(빈 시간 0)

        for (Object[] r : rows) {
            int hour = ((Number) r[0]).intValue();
            long cnt = ((Number) r[1]).longValue();
            map.put(hour, cnt);
        }
        return map;
    }

    @Override
    public Map<Integer, Long> countPolicyBreakByHour(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rows = noiseEventAnalysisRepository.countPolicyBreakByHour(start, end);

        Map<Integer, Long> map = new LinkedHashMap<>();
        for (int h = 0; h < 24; h++) map.put(h, 0L);

        for (Object[] r : rows) {
            int hour = ((Number) r[0]).intValue();
            long cnt = ((Number) r[1]).longValue();
            map.put(hour, cnt);
        }
        return map;
    }

    @Override
    public Map<SensorType, Long> countSensorType(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rows = noiseEventRepository.countSensorType(start, end);

        Map<SensorType, Long> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            SensorType type = (SensorType) r[0];
            long cnt = ((Number) r[1]).longValue();
            map.put(type, cnt);
        }
        return map;
    }

    @Override
    public Map<NoisePattern1, Long> countNoisePattern(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rows = noiseEventAnalysisRepository.countNoisePattern(start, end);

        Map<NoisePattern1, Long> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            NoisePattern1 p = (NoisePattern1) r[0];
            long cnt = ((Number) r[1]).longValue();
            map.put(p, cnt);
        }
        return map;
    }
}