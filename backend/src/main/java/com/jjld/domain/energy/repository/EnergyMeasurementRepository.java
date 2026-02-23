package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyAnalysis;
import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EnergyMeasurementRepository extends JpaRepository<EnergyMeasurement, Long> {
    // 특정 설비의 최신 측정값 조회 (필요시 사용)
    Optional<EnergyMeasurement> findTopByEnergyDeviceOrderByCreatedAtDesc(EnergyDevice device);
}