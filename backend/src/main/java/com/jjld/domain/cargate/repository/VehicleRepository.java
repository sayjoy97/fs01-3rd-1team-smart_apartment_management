package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 차번호를 이용해 조회
    Optional<Vehicle> findByPlateNumber(String plateNumber);
}
