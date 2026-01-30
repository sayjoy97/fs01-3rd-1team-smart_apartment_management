package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.*;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CargateDAO {

    // 기간내 유형별 출입기록 리스트
    Map<VehicleType, Long> getEntryCountByVehicleType( LocalDateTime start, LocalDateTime end);

    // 페이지&개수만큼의 리스트 호출
    Page<CargateEventLog> findAllCargateEventLogs(Pageable pageable);

    // 아이디별 상세조회
    CargateEventLog findCargateLogById(Long cargate_event_log_id);

    // 아이디별 출입기록 조회 리스트
    List<ParkingSession> findByVehicleIdList(Long vehicle_id);

    // 기존 차량이 없다면 신규등록
    Vehicle newVehicle(String plateNumber, VehicleType vehicleType);

    // 차번호로 차량찾기
    Optional<Vehicle> findByPlateNumber(String plateNumber);

    // 세대 등록차량 등록
    RegisteredCar createRegisteredCar(RegisteredCar registeredCar);

    // 관리자 승인차량 등록
    ApprovedCar createApprovedCar(ApprovedCar approvedCar);

    // 세대 등록차량 조회
    List<RegisteredCar> findRegisteredList();

    // 세대 등록차량 상세조회
    RegisteredCar findRegisteredCarById(Long car_id);

    // 세대 등록차량 정보수정
    RegisteredCar updateRegisteredCar(RegisteredCar regisEntity);

    // 세대 등록차량 정보삭제
    boolean deleteByRegisteredCar(Long vehicle_id);

    // 관리자 승인차량 조회
    List<ApprovedCar> findApprovedList();

    // 관리자 승인차량 상세조회

    // 관리자 승인차량 정보수정

    // 관리자 승인차량 정보삭제

}
