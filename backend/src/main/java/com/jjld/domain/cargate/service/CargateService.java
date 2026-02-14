package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dto.*;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.Vehicle;
import com.jjld.global.mqtt.handler.cargate.CargateServiceType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CargateService {

    // 최근 7일 차량 출입현황 리스트 조회
    Map<LocalDate, Map<VehicleType, Long>> getLast7DaysEntryStats();

    // 페이지&개수만큼의 리스트 호출
    Page<EntryExitRecordResponse> getRecordList(int size, int page);

    // 로그기록별 상세조회
    LogDetailBaseResponse getLogDetail(Long logId);

    // 로그기록 내 정보수정
    void updateVehicleByLog( Long cargateEventId, VehicleRelatedRequest req);

    // 차량정보 등록(세대 차량등록과 관리자 승인차량 한번에 관리)
    Long registerVehicle(VehicleRelatedRequest req);

    // 세대 차량등록 처리
    void registerHouseVehicle(Vehicle vehicle, VehicleRelatedRequest req);

    // 관리자 승인차량 등록 처리
    void registerApprovedVehicle(Vehicle vehicle, VehicleRelatedRequest req);

    // 세대 등록차량 조회
    List<RegisCarResponse> getRegisteredCars();

    // 세대 등록차량 상세정보 조회
    RegisCarDetailResponse getRegisCarDetail(Long vehicle_id);

    // 세대 등록차량 정보수정 (필요할까?)

    // 세대 등록차량 정보삭제
    boolean deleteRegisCar(Long vehicle_id);

    // 관리자 승인차량 조회 리스트
    List<ApprovedCarResponse> ApprovedCarList();

    // 관리자 승인차량 상세정보 조회
    ApprovedCarDetailResponse getApprovedCarDetail(Long vehicle_id);

    // 관리자 승인차량 수정
    void updateApprovedCar(Long vehicle_id, ApprovedCarRequest req);

    // 관리자 승인차량 삭제
    Boolean deleteApprovedCar(Long vehicle_id);

    // 입출차 처리
    void AddToTheAccessLog(String payload, CargateServiceType serviceType);

    // 요금 정산완료시 처리
    void FeeSettlement(String payload, CargateServiceType serviceType);

}
