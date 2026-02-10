package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.repository.CargateEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CargateEventLogDAOImpl implements CargateEventLogDAO {
    // 차량 출입기록 로그 테이블 관련 repogitory
    private final CargateEventLogRepository cargateEventLogRepository;

    // 페이지&개수만큼의 리스트 호출
    @Override
    public Page<CargateEventLog> findAllCargateEventLogs(Pageable pageable) {
        return cargateEventLogRepository.findAll(pageable);
    }

    // 유형별 로그아이디별 상세조회
    @Override
    public CargateEventLog DetailInfoById(Long cargate_event_log_id) {
        return cargateEventLogRepository.findDetailById(cargate_event_log_id)
                .orElseThrow(() -> new IllegalArgumentException("로그 없음"));
    }

    // 로그아이디별 상세조회
    @Override
    public CargateEventLog findByLogId(Long cargate_event_log_id) {
        return cargateEventLogRepository.findById(cargate_event_log_id)
                .orElseThrow(() -> new IllegalArgumentException("출입 로그 없음"));
    }

    @Override
    public CargateEventLog findCargateEventLogById(Long id) {
        return cargateEventLogRepository.findByCargateEventId(id);
    }

    // 기록 추가
    @Override
    public CargateEventLog createCargateLog(CargateEventLog entity) {
        return cargateEventLogRepository.save(entity);
    }


}
