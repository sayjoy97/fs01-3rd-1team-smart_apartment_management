package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyControlLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnergyControlLogRepository extends JpaRepository<EnergyControlLog, Long> {
    List<EnergyControlLog> findByEnergyDeviceDeviceIdOrderByControlledAtDesc(Long deviceId);
}
