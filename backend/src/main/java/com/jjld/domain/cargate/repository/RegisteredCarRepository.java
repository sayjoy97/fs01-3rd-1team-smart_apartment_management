package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.RegisteredCar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisteredCarRepository extends JpaRepository<RegisteredCar, Long> {

    // vehicle.vehicle_id로 조회
    Optional<RegisteredCar> findByVehicle_VehicleId(long id);
}
