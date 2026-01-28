package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.CargateEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargateRepository extends JpaRepository<CargateEventLog, Long> {

    // 기간내 일자 + 차량유형별 출입 카운트

    // 페이지&개수만큼의 데이터 호출
    Page<CargateEventLog> findAll(Pageable pageable);

    // 로그아이디별 상세조회
    CargateEventLog findByCargateEventId(Long cargate_event_log_id);
}
