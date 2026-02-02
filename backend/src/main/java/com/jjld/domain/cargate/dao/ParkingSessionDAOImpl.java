package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.ParkingSession;
import com.jjld.domain.cargate.repository.ParkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParkingSessionDAOImpl implements ParkingSessionDAO {
    private final ParkingSessionRepository parkingSessionRepository;

    // 아이디별 출입기록 조회 리스트
    @Override
    public List<ParkingSession> findByVehicleIdList(Long vehicle_id) {
        return parkingSessionRepository.findByVehicle_VehicleId(vehicle_id);
    }
}
