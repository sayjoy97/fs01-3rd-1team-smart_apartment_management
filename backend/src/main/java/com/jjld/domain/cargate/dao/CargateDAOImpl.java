package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.CargateEventLog;
import com.jjld.domain.cargate.entity.Enum.GateType;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.RegisteredCar;
import com.jjld.domain.cargate.repository.CargateRepository;
import com.jjld.domain.cargate.repository.RegisteredCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CargateDAOImpl implements CargateDAO {
    // 차량 출입기록 로그 테이블 관련 repogitory
    private final CargateRepository cargateRepository;

    // 등록차량 관련 repogitory
    private final RegisteredCarRepository registeredCarRepository;

    // 기간내 유형별 출입기록 리스트
    @Override
    public Map<VehicleType, Long> getEntryCountByVehicleType(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = cargateRepository.countEntryByVehicleType(GateType.ENTRY, start, end);

        Map<VehicleType, Long> map = new EnumMap<>(VehicleType.class);
        for (Object[] row : results) {
            map.put(
                    (VehicleType) row[0], // JPQL로 설정한 SELECT문 첫번째 컬럼
                    (Long) row[1] // JPQL로 설정한 SELECT문 두번째 컬럼
            );
        }

        return map;
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

    // 차량정보 등록
    @Override
    public RegisteredCar regisVehicle(RegisteredCar regisEntity) {
        return registeredCarRepository.save(regisEntity);
    }

    // 차량정보 수정
    @Override
    public RegisteredCar updateVehicle(RegisteredCar regisEntity) {
        return registeredCarRepository.save(regisEntity);
    }

    // 차량정보 삭제
    @Override
    public boolean deleteByRegisteredCar(Long id) {
        if(!cargateRepository.existsById(id)) {
            return false;
        }
        cargateRepository.deleteById(id);
        return true;
    }
}
