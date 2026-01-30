package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.RegisteredCar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredCarRepository extends JpaRepository<RegisteredCar, Long> {

    RegisteredCar findByVehicle_VehicleId(long id);
}
