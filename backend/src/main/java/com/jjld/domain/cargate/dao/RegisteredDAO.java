package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.RegisteredCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegisteredDAO {

    // 세대 등록차량 등록
    RegisteredCar createRegisteredCar(RegisteredCar registeredCar);

    // 세대 등록차량 조회
    Page<RegisteredCar> findRegisteredList(Pageable pageable);

    // 세대 등록차량 상세조회
    RegisteredCar findByVehicle_VehicleId(Long vehicle_id);

    // 세대 등록차량 정보수정(현재 미사용중)
    RegisteredCar updateRegisteredCar(RegisteredCar regisEntity);

    // 세대 등록차량 정보삭제
    boolean deleteByRegisteredCar(Long vehicle_id);

}
