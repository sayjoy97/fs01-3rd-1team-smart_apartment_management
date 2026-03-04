package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.ApprovedCar;
import com.jjld.domain.cargate.repository.ApprovedCarRepository;
import com.jjld.domain.cargate.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ApprovedDAOImpl implements ApprovedDAO {
    private final ApprovedCarRepository approvedCarRepository;
    private final VehicleRepository vehicleRepository;

    // 관리자 승인차량 등록
    @Override
    public ApprovedCar createApprovedCar(ApprovedCar approvedCar) {
        return approvedCarRepository.save(approvedCar);
    }

    // 관리자 승인차량 조회
    @Transactional
    @Override
    public Page<ApprovedCar> ApprovedCarList(Pageable pageable) {
        Page<ApprovedCar> approvedList = approvedCarRepository.findAll(pageable);

        approvedList.forEach(ApprovedCar::refreshCurrentStatus);

        return approvedList;
    }

    // 관리자 승인차량 상세정보 조회
    @Transactional
    @Override
    public ApprovedCar findByVehicle_VehicleId(Long vehicle_id) {

        ApprovedCar findByApprovedId = approvedCarRepository.findByVehicle_VehicleId(vehicle_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 정보 없음"));

        findByApprovedId.refreshCurrentStatus();

        return findByApprovedId;
    }

    // 관리자 승인차량 수정
    @Override
    public ApprovedCar updateApprovedCar(ApprovedCar approvedCar) {
        return approvedCarRepository.save(approvedCar);
    }

    // 관리자 승인차량 삭제
    @Transactional
    @Override
    public boolean deleteByApprovedCar(Long vehicle_id) {
        if(!approvedCarRepository.existsById(vehicle_id)) {
            return false;
        }

        approvedCarRepository.deleteById(vehicle_id);
        return true;
    }
}
