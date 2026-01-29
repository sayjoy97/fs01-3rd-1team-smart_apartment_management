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
    public Map<VehicleType, Long> countByTypeList(LocalDate selectedDay) {
        return cargateRepository.getCargateEventLogs(
                        GateType.ENTRY,
                        selectedDay.atStartOfDay(),
                        selectedDay.plusDays(1).atStartOfDay()
        ).stream() // 리스트를 Stream<Object>로
                // Stream의 요소를 Map<>에 적어둔 타입(VehicleType, Long)별로 key와 value에 저장
                .collect(Collectors.toMap(
                o -> (VehicleType) o[0], // jpql에서 첫번째 SELECT값으로 지정하겠다
                o -> (Long) o[1] // jpql에서 두번째 SELECT값으로 지정하겠다
        ));
    }

    // 한번에 n일치를 보내기용 - 테스트
    @Override
    public List<Object[]> countByTypeList_test(LocalDate startDate, LocalDate endDate) {
        return cargateRepository.getLast7DaysEntryCount(
                GateType.ENTRY,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );
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
