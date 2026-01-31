package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.Vehicle;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface VehicleDAO {

    // 기간내 유형별 출입기록 리스트
    Map<VehicleType, Long> getEntryCountByVehicleType(LocalDateTime start, LocalDateTime end);

    // 기존 차량이 없다면 신규등록
    Vehicle newVehicle(String plateNumber, VehicleType vehicleType);

    // 차번호로 차량찾기
    Optional<Vehicle> findByPlateNumber(String plateNumber);

    // 차량 번호 수정 (중복 체크 권장)
    void changePlateNumber(String PlateNumber, Long vehicleId);
}
