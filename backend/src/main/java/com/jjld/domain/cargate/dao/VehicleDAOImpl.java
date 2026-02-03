package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.Enum.GateType;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.Vehicle;
import com.jjld.domain.cargate.repository.CargateEventLogRepository;
import com.jjld.domain.cargate.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VehicleDAOImpl implements VehicleDAO {
    private final VehicleRepository vehicleRepository;
    private final CargateEventLogRepository cargateEventLogRepository;

    // vehicle_id로 상세정보 조회
    @Override
    public Vehicle findByVehicleId(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디 정보 없음"));
    }
    
    // 기간내 유형별 출입기록 리스트
    @Override
    public Map<VehicleType, Long> getEntryCountByVehicleType(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = cargateEventLogRepository.countEntryByVehicleType(GateType.ENTRY, start, end);

        Map<VehicleType, Long> map = new EnumMap<>(VehicleType.class);
        for (Object[] row : results) {
            map.put(
                    (VehicleType) row[0], // JPQL로 설정한 SELECT문 첫번째 컬럼
                    (Long) row[1] // JPQL로 설정한 SELECT문 두번째 컬럼
            );
        }

        return map;
    }

    // 차번호로 차량찾기
    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return vehicleRepository.findByPlateNumber(plateNumber);
    }


    // 차량 번호 수정 (중복 체크 권장)
    @Override
    public void changePlateNumber(String PlateNumber, Long vehicleId) {
        vehicleRepository.findByPlateNumber(PlateNumber)
                .filter(v -> !v.getVehicleId().equals(vehicleId))
                .ifPresent(v -> {
                    throw new IllegalStateException("이미 등록된 차량 번호");
                });
    }

    // 기존 차량이 없다면 신규등록
    @Override
    public Vehicle newVehicle(String plateNumber, VehicleType vehicleType) {
        return vehicleRepository.save(new Vehicle(plateNumber, vehicleType));
    }
}
