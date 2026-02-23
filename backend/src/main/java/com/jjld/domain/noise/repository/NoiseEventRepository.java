package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.Enum.SensorType;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoiseEventRepository extends JpaRepository<NoiseEvent, Long> {
    // 오늘 발생 이벤트 수
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 기간 내 이벤트 목록 (페이지네이션)
    Page<NoiseEvent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 센서 기준 이벤트 조회 (통계 / 분석 용으로)
    List<NoiseEvent> findByNoiseSensor(NoiseSensor noiseSensor);

    // 모든 이벤트 집계/통계용
    List<NoiseEvent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 특정센서에서 특정시간범위 내에 특정센서타입의 이벤트가 존재하는지 여부
    boolean existsByNoiseSensorAndNoiseSensor_SensorTypeInAndCreatedAtBetween(
            NoiseSensor sensor,
            List<SensorType> sensorTypes,
            LocalDateTime start,
            LocalDateTime end
    );

    // 최근 N초 내 같은 센서 이벤트 수
    long countByNoiseSensorAndCreatedAtAfter(NoiseSensor sensor, LocalDateTime after);

    @Query("""
        select hour(e.createdAt), count(e)
        from NoiseEvent e
        where e.createdAt >= :start and e.createdAt < :end
        group by hour(e.createdAt)
        order by hour(e.createdAt)
    """)
    List<Object[]> countByHour(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query("""
        select s.sensorType, count(e)
        from NoiseEvent e
        join e.noiseSensor s
        where e.createdAt >= :start and e.createdAt < :end
        group by s.sensorType
    """)
    List<Object[]> countSensorType(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
}
