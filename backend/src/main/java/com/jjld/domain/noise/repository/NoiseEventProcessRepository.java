package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseEventProcess;
import com.jjld.domain.noise.entity.NoiseSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoiseEventProcessRepository extends JpaRepository<NoiseEventProcess, Long> {
    // 이벤트별 처리 정보 조회
    Optional<NoiseEventProcess> findByNoiseEvent(NoiseEvent noiseEvent);

    // 승인 대기 이벤트 수 (전체)
    long countByStatus(ProcessStatus status);

    // 오늘 승인 대기 이벤트 수
    long countByStatusAndCreatedAtBetween(ProcessStatus status, LocalDateTime start, LocalDateTime end);

    // 전체/상태별 목록
    Page<NoiseEventProcess> findByStatus(ProcessStatus status, Pageable pageable);

    // 즉시 처리 필요 + 승인 대기 목록
    Page<NoiseEventProcess> findByUrgentBreakTrueAndStatus(ProcessStatus status, Pageable pageable);

    Optional<NoiseEventProcess> findByNoiseEvent_NoiseEventId(Long noiseEventId);

    // 최근 N시간 내 같은 센서 이벤트 수 (재발 태그 계산용)
    long countByNoiseEvent_NoiseSensorAndStatusAndCreatedAtAfter(NoiseSensor sensor, ProcessStatus status, LocalDateTime after);

    // 최근 N일간 긴급 이벤트 수 (상습 태그 계산용)
    long countByNoiseEvent_NoiseSensorAndUrgentBreakTrueAndCreatedAtAfter(NoiseSensor sensor, LocalDateTime after);

    // 상습구간 상세 타임라인 (페이지네이션)
    Page<NoiseEventProcess> findByNoiseEvent_NoiseSensorAndCreatedAtAfterOrderByCreatedAtDesc(
            NoiseSensor sensor, LocalDateTime after, Pageable pageable);

    // 상습구간 상세 계산전용(횟수, 평균 dB)
    List<NoiseEventProcess>
    findByNoiseEvent_NoiseSensorAndUrgentBreakTrueAndCreatedAtAfter(
            NoiseSensor sensor,
            LocalDateTime after
    );

    // 주/야 필터 + 페이지네이션을 서비스에서 "수동"으로 하려면,
    // Pageable로 DB에서 잘라오면 필터 후 페이지가 깨짐.
    // 그래서 일단 정렬된 List로 가져온 뒤, 서비스에서 필터+페이징 한다.
    List<NoiseEventProcess> findAll(Sort sort);

    List<NoiseEventProcess> findByStatus(ProcessStatus status, Sort sort);

    @Query("""
        select p
        from NoiseEventProcess p
        where (:status is null or p.status = :status)
          and (
                :viewMode = 'all'
                or (:viewMode = 'day'
                    and function('TIME', p.noiseEvent.createdAt) >= :dayStart
                    and function('TIME', p.noiseEvent.createdAt) < :nightStart
                )
                or (:viewMode = 'night'
                    and not (
                        function('TIME', p.noiseEvent.createdAt) >= :dayStart
                        and function('TIME', p.noiseEvent.createdAt) < :nightStart
                    )
                )
          )
    """)
    Page<NoiseEventProcess> findForList(
            @Param("status") ProcessStatus status,
            @Param("viewMode") String viewMode,
            @Param("dayStart") LocalTime dayStart,
            @Param("nightStart") LocalTime nightStart,
            Pageable pageable
    );
}
