package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.Enum.ProcessStatus;
import com.jjld.domain.noise.entity.NoiseEvent;
import com.jjld.domain.noise.entity.NoiseEventProcess;
import com.jjld.domain.noise.entity.NoiseSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    long countByNoiseEvent_NoiseSensorAndCreatedAtAfter(NoiseSensor sensor, ProcessStatus status, LocalDateTime after);

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
}
