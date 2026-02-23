package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Enum.GateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CargateEventLogDAO {

    // 페이지&개수만큼의 리스트 호출
    Page<CargateEventLog> findAllCargateEventLogs(Pageable pageable);

    // 유형별 로그아이디별 상세조회
    CargateEventLog DetailInfoById(Long cargate_event_log_id);

    // 로그아이디별 상세조회
    CargateEventLog findByLogId(Long cargate_event_log_id);

    // vehicle_id별 가장 최근 데이터 가져오기(ENTRY)
    CargateEventLog findByVehicleIdTypeEntry(Long vehicle_id);

    CargateEventLog findCargateEventLogById(Long id);

    // 기록 추가
    CargateEventLog createCargateLog(CargateEventLog entity);

    // 출차 로그기록 조회
    CargateEventLog findVehicleByType(Long vehicleId, GateType gateType);
}
