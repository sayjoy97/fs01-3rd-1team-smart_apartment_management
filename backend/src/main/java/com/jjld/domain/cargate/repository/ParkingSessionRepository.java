package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.cargate.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession,Long> {

    // vehicle.vehicle_id로 입출차 기록 출력
    List<ParkingSession> findByVehicle_VehicleId(Long vehicleId);

    // vehicle_id로 현재 입차중인 차량정보 하나만 호출
    @Query("""
        select ps
        from ParkingSession ps
        where ps.vehicle.vehicleId = :vehicleId
        and ps.status = :status
        and ps.exitAt is null
    """)
    ParkingSession findSessionDataByStatus(@Param("vehicleId") Long vehicleId, @Param("status")ParkingStatus parkingStatus);
}
