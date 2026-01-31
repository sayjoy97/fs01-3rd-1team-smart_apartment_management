package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.ParkingSession;

import java.util.List;

public interface ParkingSessionDAO {
    // 아이디별 출입기록 조회 리스트
    List<ParkingSession> findByVehicleIdList(Long vehicle_id);
}
