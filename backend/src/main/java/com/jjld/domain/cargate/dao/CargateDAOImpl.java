package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.repository.CargateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CargateDAOImpl implements CargateDAO {
    private final CargateRepository cargateRepository;

    // 기간내 유형별 출입기록 리스트

    // 페이지&개수만큼의 리스트 호출
    @Override
    public Page<CargateEventLog> findAllCargateEventLogs(Pageable pageable) {
        return cargateRepository.findAll(pageable);
    }

    // 아이디별 상세조회
    @Override
    public CargateEventLog findCargateLogById(Long cargate_event_log_id) {
        return cargateRepository.findByCargateEventId(cargate_event_log_id);
    }
}
