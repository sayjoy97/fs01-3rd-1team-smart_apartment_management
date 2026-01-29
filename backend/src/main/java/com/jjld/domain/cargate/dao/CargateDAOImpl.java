package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Enum.GateType;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.repository.CargateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CargateDAOImpl implements CargateDAO {
    private final CargateRepository cargateRepository;

    // 기간내 유형별 출입기록 리스트
    @Override
    public Map<VehicleType, Long> countByTypeList(LocalDate selectedDay) {
        return cargateRepository.getCargateEventLogs(
                selectedDay.atStartOfDay(), selectedDay.plusDays(1).atStartOfDay()
        ).stream() // 리스트를 Stream<Object>로
                // Stream의 요소를 Map<>에 적어둔 타입(VehicleType, Long)별로 key와 value에 저장
                .collect(Collectors.toMap(
                o -> (VehicleType) o[0], // jpql에서 첫번째 SELECT값으로 지정하겠다
                o -> (Long) o[1] // jpql에서 두번째 SELECT값으로 지정하겠다
        ));
    }

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
