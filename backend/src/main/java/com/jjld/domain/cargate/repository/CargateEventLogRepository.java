package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Enum.GateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CargateEventLogRepository extends JpaRepository<CargateEventLog, Long> {

    // 기간내 일자 + 차량유형별 출입 카운트
    @Query("""
        SELECT v.vehicleType, COUNT(e)
        FROM CargateEventLog e
        JOIN e.vehicle v
        WHERE e.gateType = :gateType
          AND e.eventAt >= :start
          AND e.eventAt < :end
        GROUP BY v.vehicleType
    """)
    List<Object[]> countEntryByVehicleType(
            @Param("gateType") GateType gateType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 페이지&개수만큼의 데이터 호출
    Page<CargateEventLog> findAll(Pageable pageable);

    // 로그아이디별 상세조회
    CargateEventLog findByCargateEventId(Long cargate_event_log_id);
}
