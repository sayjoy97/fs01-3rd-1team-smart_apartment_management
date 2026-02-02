package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.ApprovedCar;

import java.util.List;

public interface ApprovedDAO {

    // 관리자 승인차량 등록
    ApprovedCar createApprovedCar(ApprovedCar approvedCar);

    // 관리자 승인차량 조회
    List<ApprovedCar> ApprovedCarList();

    // 관리자 승인차량 상세정보 조회
    ApprovedCar findByVehicle_VehicleId(Long vehicle_id);

    // 관리자 승인차량 수정
    ApprovedCar updateApprovedCar(ApprovedCar approvedCar);

    // 관리자 승인차량 삭제
    boolean deleteByApprovedCar(Long vehicle_id);
}
