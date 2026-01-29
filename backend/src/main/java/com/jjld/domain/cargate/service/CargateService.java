package com.jjld.domain.cargate.service;

import com.jjld.domain.cargate.dto.DailyVehicleTypeCountResponse;
import com.jjld.domain.cargate.dto.EntryExitRecordResponse;
import com.jjld.domain.cargate.dto.RecordDetailResponse;
import com.jjld.domain.cargate.dto.VehicleRegisterRequest;
import com.jjld.domain.cargate.entity.Vehicle;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CargateService {

    // 최근 7일 차량 출입현황 리스트 조회
    List<DailyVehicleTypeCountResponse> getDailyVehicleTypeCountList();

    // 최근 7일 차량 출입현황 리스트 조회 - repo단에서 한번에 호출하는 방식(테스트)
    List<DailyVehicleTypeCountResponse>  getDailyVehicleTypeCountList_test();

    // 페이지&개수만큼의 리스트 호출
    Page<EntryExitRecordResponse> getRecordList(int size, int page);

    // 로그아이디 별 상세조회
    RecordDetailResponse getDetailInfo(Long cargate_event_log_id);

    // 차량정보 등록
    Long registerVehicle(VehicleRegisterRequest req);

    // 세대 차량등록 처리
    void registerHouseVehicle(Vehicle vehicle, VehicleRegisterRequest req);

    // 관리자 승인차량 등록 처리
    void registerApprovedVehicle(Vehicle vehicle, VehicleRegisterRequest req);

    // 차량정보 수정
    VehicleRegisterRequest updateCar(Long cargateEventId, VehicleRegisterRequest request);

    // 차량정보 삭제
    boolean deleteCar(Long cargateEventId);
}
