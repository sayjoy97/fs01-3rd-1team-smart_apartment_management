package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import com.jjld.domain.cargate.entity.RegisteredCar;
import com.jjld.domain.cargate.repository.RegisteredCarRepository;
import com.jjld.domain.cargate.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegisteredDAOImpl implements RegisteredDAO {
    private final RegisteredCarRepository registeredCarRepository;
    private final VehicleRepository vehicleRepository;

    // 세대 등록차량 등록
    @Override
    public RegisteredCar createRegisteredCar(RegisteredCar registeredCar) {
        return registeredCarRepository.save(registeredCar);
    }

    // 세대 등록차량 조회
    @Override
    public List<RegisteredCar> findRegisteredList() {
        return registeredCarRepository.findAll();
    }

    // 세대 등록차량 상세조회
    @Override
    public RegisteredCar findByVehicle_VehicleId(Long vehicle_id) {
        return registeredCarRepository.findByVehicle_VehicleId(vehicle_id)
                .orElseThrow(() -> new IllegalArgumentException("해당정보 없음."));
    }

    // 세대 등록차량 정보수정(만들어놨는데, 필요없으면 지울듯?)
    @Override
    public RegisteredCar updateRegisteredCar(RegisteredCar regisEntity) {
        return registeredCarRepository.save(regisEntity);
    }

    // 세대 등록차량 정보삭제
    @Transactional
    @Override
    public boolean deleteByRegisteredCar(Long vehicle_id) {
        if (!registeredCarRepository.existsById(vehicle_id)) {
            return false;
        }

        // vehicle테이블에서는 없애지 않고 미등록차량으로 변경
        vehicleRepository.updateVehicleType(vehicle_id, VehicleType.UNREGISTERED);

        registeredCarRepository.deleteById(vehicle_id);
        return true;
    }
}
