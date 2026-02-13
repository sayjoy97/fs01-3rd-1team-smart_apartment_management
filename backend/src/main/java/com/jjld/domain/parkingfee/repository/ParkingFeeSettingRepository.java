package com.jjld.domain.parkingfee.repository;

import com.jjld.domain.parkingfee.entity.ParkingFeeSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ParkingFeeSettingRepository extends JpaRepository<ParkingFeeSetting, Long> {

    // 특정 날짜에 적용된 주차요금 정보 조회
    @Query("""
        select pfs
        from ParkingFeeSetting pfs
        where pfs.appliedAt <= :targetDate
        order by pfs.appliedAt desc
        limit 1
    """)
    ParkingFeeSetting findAppliedSetting(@Param("targetDate") LocalDateTime targetDate);

    // 활성화된 주차요금 1건 조회
    Optional<ParkingFeeSetting> findFirstByActiveTrue();
}
