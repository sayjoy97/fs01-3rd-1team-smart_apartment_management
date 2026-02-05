package com.jjld.domain.parkingfee.repository;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.parkingfee.entity.ParkingFeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ParkingFeeHistoryRepository extends JpaRepository<ParkingFeeHistory, Long> {
    @Query("""
        SELECT COALESCE(SUM(history.totalCharge), 0)
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

    // 월 평균금액 조회
    @Query(value = """
        select avg(month_sum) as monthly_avg
        from (
            select sum(total_charge) as month_sum
            from parking_fee_history
            where charged_at >= date_sub(curdate(), interval 12 month)
            group by year(charged_at), month(charged_at)
        ) t
    """, nativeQuery = true)
    BigDecimal findAverageBy12Month();

    // 일일 최고금액 조회
    @Query("select max(feeHistory.totalCharge) from ParkingFeeHistory feeHistory")
    long findMaxCharge();

    // 최근 30일 일별 누적금액 조회
    @Query("""
        select date(ps.chargedAt), sum(ps.totalCharge)
        from ParkingFeeHistory ps
        where ps.chargedAt >= :start
        group by date(ps.chargedAt)
        order by date(ps.chargedAt)
    
    """)
    List<Object[]> findDailyTotalAmount(@Param("start") LocalDateTime start);

    // 최근 12개월 월별 누적금액 조회
    @Query("""
        select year(ps.chargedAt), month(ps.chargedAt),sum(ps.totalCharge), avg(ps.totalCharge)
        from ParkingFeeHistory ps
        where ps.chargedAt >= :startMonth
        group by year(ps.chargedAt), month(ps.chargedAt)
        order by year(ps.chargedAt), month(ps.chargedAt)
    """)
    List<Object[]> findMonthlyTotalAmount(@Param("startMonth") LocalDateTime startMonth);
}
