package com.jjld.domain.garden.repository;

import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.SensorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorLogRepository extends JpaRepository<SensorLog, Long> {
    Optional<SensorLog> findTopByDeviceOrderByCreatedAtDesc(Device device);
}
