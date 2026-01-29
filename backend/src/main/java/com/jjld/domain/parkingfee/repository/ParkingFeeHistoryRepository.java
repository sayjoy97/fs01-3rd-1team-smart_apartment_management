package com.jjld.domain.parkingfee.repository;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.parkingfee.entity.ParkingFeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ParkingFeeHistoryRepository extends JpaRepository<ParkingFeeHistory, Long> {
    @Query("""
        SELECT count(history.totalCharge)
        FROM ParkingFeeHistory history
        WHERE history.parkingSession.status = :parkingStatus
        AND history.paid = true
        AND history.chargedAt BETWEEN :start AND :end
    """)
    long getCountBySettingDay(
            @Param("parkingStatus")ParkingStatus parkingStatus,
            @Param("start") LocalDateTime start,
            @Param("end")LocalDateTime end
    );
}
