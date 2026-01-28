package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Enum.GateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface CargateDAO {

    // 기간내 유형별 출입기록 리스트

    // 페이지&개수만큼의 리스트 호출
    Page<CargateEventLog> findAllCargateEventLogs(Pageable pageable);

    // 아이디별 상세조회
    CargateEventLog findCargateLogById(Long cargate_event_log_id);
}
