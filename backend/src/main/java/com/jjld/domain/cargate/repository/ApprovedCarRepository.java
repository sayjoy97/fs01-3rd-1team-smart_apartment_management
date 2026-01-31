package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.ApprovedCar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovedCarRepository extends JpaRepository<ApprovedCar, Long> {
    // vehicle_id로 상세정보 조회
    Optional<ApprovedCar> findByVehicle_VehicleId(Long vehicleId);
}
