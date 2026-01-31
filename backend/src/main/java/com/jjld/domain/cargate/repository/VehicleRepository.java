package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Vehicle findByVehicleId(Long vehicle_id);

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    // 특정 조건에 따라 차량 타입을 바꾸는 JPQL
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Vehicle v
        SET v.vehicleType = :vehicleType
        WHERE v.vehicleId = :vehicleId
    """)
    int updateVehicleType(
            @Param("vehicleId") Long vehicleId,
            @Param("vehicleType") VehicleType vehicleType
    );
}
