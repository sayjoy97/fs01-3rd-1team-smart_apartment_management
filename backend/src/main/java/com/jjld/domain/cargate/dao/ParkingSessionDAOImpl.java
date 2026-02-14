package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.ParkingSession;
import com.jjld.domain.cargate.repository.ParkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    // vehicle_id로 현재 입차중인 차량 정보만 조회
    @Override
    public ParkingSession findByVehicleIdEntryStatus(Long vehicle_id) {
        return parkingSessionRepository.findSessionDataByStatus(vehicle_id, ParkingStatus.IN);
    }

    // 추가
    @Override
    public ParkingSession createSessionInfo(ParkingSession parkingSession) {
        return parkingSessionRepository.save(parkingSession);
    }

    // 상태 출차로 수정
    @Override
    public void exitVehicleStatus(ParkingSession entity) {
        parkingSessionRepository.save(entity);
    }

    // 월평균 방문(미등록)차량 조회
    @Override
    public long getUnRegisAverageCount(VehicleType vehicleType, LocalDateTime start) {
        return parkingSessionRepository.findMonthlyAvgCount(vehicleType, start);
    }
}
