package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyDeviceRepository extends JpaRepository<EnergyDevice, Long> {
    long countByDeviceStatus(DeviceStatus status);
}
