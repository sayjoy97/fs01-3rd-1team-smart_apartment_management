package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.CargateEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CargateEventLogDAO {

    // 페이지&개수만큼의 리스트 호출
    Page<CargateEventLog> findAllCargateEventLogs(Pageable pageable);

    // 유형별 로그아이디별 상세조회
    CargateEventLog DetailInfoById(Long cargate_event_log_id);

    // 로그아이디별 상세조회
    CargateEventLog findByLogId(Long cargate_event_log_id);

    CargateEventLog findCargateEventLogById(Long id);

    // 기록 추가
    CargateEventLog createCargateLog(CargateEventLog entity);

}
