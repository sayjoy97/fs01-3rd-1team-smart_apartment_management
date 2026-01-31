package com.jjld.domain.parkingfee.repository;

import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingFeeSettingRepository extends JpaRepository<ParkingFeeSetting, Long> {

    Optional<ParkingFeeSetting> findFirstByActiveTrue();
}
