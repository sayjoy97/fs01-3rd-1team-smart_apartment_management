package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession,Long> {

    // vehicle.vehicle_id로 입출차 기록 출력
    List<ParkingSession> findByVehicle_VehicleId(Long vehicleId);
}
