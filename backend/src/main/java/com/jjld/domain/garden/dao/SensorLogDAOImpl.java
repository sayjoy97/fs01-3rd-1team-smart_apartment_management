package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.SensorLog;
import com.jjld.domain.garden.repository.SensorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SensorLogDAOImpl implements SensorLogDAO {
    private final SensorLogRepository sensorLogRepository;

    // 최근 센서 데이터 조회
    @Override
    public Optional<SensorLog> getCurrentSensorLog(Device device) {
        return sensorLogRepository.findTopByDeviceOrderByCreatedAtDesc(device);
    }
}
