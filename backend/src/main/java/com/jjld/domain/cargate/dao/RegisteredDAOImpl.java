package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.RegisteredCar;
import com.jjld.domain.cargate.repository.RegisteredCarRepository;
import com.jjld.domain.cargate.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<RegisteredCar> findRegisteredList(Pageable pageable) {
        return registeredCarRepository.findAll(pageable);
    }

    // 세대 등록차량 상세조회
    @Override
    public RegisteredCar findByVehicle_VehicleId(Long vehicle_id) {
        return registeredCarRepository.findByVehicle_VehicleId(vehicle_id)
                .orElseThrow(() -> new IllegalArgumentException("해당정보 없음."));
    }

    // 세대 등록차량 정보수정
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

        registeredCarRepository.deleteById(vehicle_id);
        return true;
    }
}
