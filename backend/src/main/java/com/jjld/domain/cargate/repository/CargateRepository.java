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

public interface CargateRepository extends JpaRepository<CargateEventLog, Long> {

    // 기간내 일자 + 차량유형별 출입 카운트
    @Query("""
        SELECT log.vehicle.vehicleType, count(log.cargateEventId)
        FROM CargateEventLog log
        WHERE log.gateType = :gateType
        AND log.eventAt >= :start
        AND log.eventAt < :end
        GROUP BY log.vehicle.vehicleType
    """)
    List<Object[]> getCargateEventLogs(
            @Param("gateType") GateType gateType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 한번에 n일치를 다 보내는 쿼리
    @Query("""
        SELECT 
            DATE(log.eventAt),
            log.vehicle.vehicleType,
            COUNT(log.cargateEventId)
        FROM CargateEventLog log
        WHERE log.gateType = :gateType
        AND log.eventAt >= :start
        AND log.eventAt < :end
        GROUP BY DATE(log.eventAt), log.vehicle.vehicleType
    """)
    List<Object[]> getLast7DaysEntryCount(
            @Param("gateType") GateType gateType,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 페이지&개수만큼의 데이터 호출
    Page<CargateEventLog> findAll(Pageable pageable);

    // 로그아이디별 상세조회
    CargateEventLog findByCargateEventId(Long cargate_event_log_id);
}
