package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergySavingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnergySavingResultRepository extends JpaRepository<EnergySavingResult, Long> {
    // 아직 절감 계산이 완료되지 않은 가장 최근 제어 기록 1개 조회
    Optional<EnergySavingResult> findTopByEnergyDeviceAndAfterKwhIsNullOrderByEvaluatedAtDesc(EnergyDevice device);

    // 절감 결과 조회
    Page<EnergySavingResult> findByEnergyDeviceDeviceIdOrderByEvaluatedAtDesc(
            Long deviceId, Pageable pageable);

    // 미완료 존재 여부 (중복 방지)
    boolean existsByEnergyDeviceAndAfterKwhIsNull(EnergyDevice energyDevice);
}
