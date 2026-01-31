package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.*;
import com.jjld.domain.cargate.entity.Enum.GateType;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CargateDAOImpl implements CargateDAO {
    // 차량 출입기록 로그 테이블 관련 repogitory
    private final CargateEventLogRepository cargateRepository;

    // 등록차량 관련 repogitory
    private final RegisteredCarRepository registeredCarRepository;
    private final ApprovedCarRepository approvedCarRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;

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

    // 로그아이디별 상세조회
    @Override
    public CargateEventLog findCargateLogById(Long cargate_event_log_id) {
        return cargateRepository.findByCargateEventId(cargate_event_log_id);
    }

    // 아이디별 출입기록 조회 리스트
    @Override
    public List<ParkingSession> findByVehicleIdList(Long vehicle_id) {
        return parkingSessionRepository.findByVehicle_VehicleId(vehicle_id);
    }

    // 차번호로 차량찾기
    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return vehicleRepository.findByPlateNumber(plateNumber);
    }

    // 기존 차량이 없다면 신규등록
    @Override
    public Vehicle newVehicle(String plateNumber, VehicleType vehicleType) {
        return vehicleRepository.save(new Vehicle(plateNumber, vehicleType));
    }

    // 세대 등록차량 등록
    @Override
    public RegisteredCar createRegisteredCar(RegisteredCar registeredCar) {
        return registeredCarRepository.save(registeredCar);
    }

    // 관리자 승인차량 등록
    @Override
    public ApprovedCar createApprovedCar(ApprovedCar approvedCar) {
        return approvedCarRepository.save(approvedCar);
    }

    // 세대 등록차량 조회
    @Override
    public List<RegisteredCar> findRegisteredList() {
        return registeredCarRepository.findAll();
    }

    // 세대 등록차량 상세조회
    @Override
    public RegisteredCar findRegisteredCarById(Long vehicle_id) {
        return registeredCarRepository.findByVehicle_VehicleId(vehicle_id);
    }

    // 세대 등록차량 정보수정(만들어놨는데, 필요없으면 지울듯?)
    @Override
    public RegisteredCar updateRegisteredCar(RegisteredCar regisEntity) {
        return registeredCarRepository.save(regisEntity);
    }

    // 세대 등록차량 정보삭제
    @Override
    public boolean deleteByRegisteredCar(Long vehicle_id) {
        if (!registeredCarRepository.existsById(vehicle_id)) {
            return false;
        }
        registeredCarRepository.deleteById(vehicle_id);
        return true;
    }

    // 관리자 승인차량 조회
    @Transactional
    @Override
    public List<ApprovedCar> ApprovedCarList() {
        List<ApprovedCar> approvedList = approvedCarRepository.findAll();

        approvedList.forEach(ApprovedCar::refreshCurrentStatus);

        return approvedList;
    }

    // 관리자 승인차량 상세정보 조회
    @Transactional
    @Override
    public ApprovedCar findApprovedCarById(Long vehicle_id) {

        ApprovedCar findByApprovedId = approvedCarRepository.findByVehicle_VehicleId(vehicle_id);

        findByApprovedId.refreshCurrentStatus();

        return findByApprovedId;
    }

    // 관리자 승인차량 수정
    @Override
    public ApprovedCar updateApprovedCar(ApprovedCar approvedCar) {
        return approvedCarRepository.save(approvedCar);
    }

    // 관리자 승인차량 삭제
    @Override
    public boolean deleteByApprovedCar(Long vehicle_id) {
        if(!approvedCarRepository.existsById(vehicle_id)) {
            return false;
        }
        approvedCarRepository.deleteById(vehicle_id);
        return true;
    }


}
