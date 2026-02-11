package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyAnalysis;
import com.jjld.domain.energy.entity.Enum.AnalysisStatus;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
@Repository
public interface EnergyAnalysisRepository extends JpaRepository<EnergyAnalysis, Long> {
    // 오늘 점검 필요 건수
    @Query("SELECT COUNT(e) FROM EnergyAnalysis e " +
            "WHERE e.analysisStatus = :status " +
            "AND e.analyzedAt BETWEEN :start AND :end")
    long countByStatusToday(@Param("status") AnalysisStatus status,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    // 설비별 최신 분석 중, 점검 권장 상태인 분석만 조회
    @Query("""
            SELECT ea
            FROM EnergyAnalysis ea
            WHERE ea.analysisStatus = :status
              AND ea.analyzedAt = (
                    SELECT MAX(ea2.analyzedAt)
                    FROM EnergyAnalysis ea2
                    WHERE ea2.energyDevice = ea.energyDevice
              )
            """)
    Page<EnergyAnalysis> findLatestByStatus(@Param("status") AnalysisStatus status, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(e.estimatedWasteCost), 0)
        FROM EnergyAnalysis e
        WHERE e.analysisStatus = :status
          AND e.analyzedAt = (
                SELECT MAX(e2.analyzedAt)
                FROM EnergyAnalysis e2
                WHERE e2.energyDevice = e.energyDevice
          )
        """)
    double sumEstimatedWasteCost(@Param("status") AnalysisStatus status);

    @Query("""
        SELECT e
        FROM EnergyAnalysis e
        WHERE e.energyDevice.deviceId = :deviceId
        ORDER BY e.analyzedAt DESC
        """)
    Page<EnergyAnalysis> findLatestByDeviceId(@Param("deviceId") Long deviceId, Pageable pageable);

    @Query("""
    SELECT ea
    FROM EnergyAnalysis ea
    WHERE ea.analyzedAt = (
        SELECT MAX(e2.analyzedAt)
        FROM EnergyAnalysis e2
        WHERE e2.energyDevice = ea.energyDevice
    )
    AND (:status IS NULL OR ea.energyDevice.deviceStatus = :status)
    ORDER BY ea.estimatedWasteCost DESC
""")
    Page<EnergyAnalysis> findLatestAnalysisByStatus(
            @Param("status") DeviceStatus status,
            Pageable pageable
    );
}
