package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.ParkingSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ParkingSessionDAO {
    // 아이디별 출입기록 조회 리스트
    List<ParkingSession> findByVehicleIdList(Long vehicle_id);

    // 월평균 방문(미등록)차량 조회
    long getUnRegisAverageCount(VehicleType vehicleType, LocalDateTime start);
}
