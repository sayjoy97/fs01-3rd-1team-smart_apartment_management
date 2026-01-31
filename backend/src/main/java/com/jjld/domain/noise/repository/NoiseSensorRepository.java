package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.Enum.SensorType;
import com.jjld.domain.noise.entity.NoiseSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoiseSensorRepository extends JpaRepository<NoiseSensor, Long> {
    // 활성 센서 조회
    List<NoiseSensor> findByIsActiveTrue();

    // 센서 타입별 조회 (통계용)
    List<NoiseSensor> findBySensorType(SensorType sensorType);
}
