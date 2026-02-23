package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.DeviceType;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyUsageSummaryRepository extends JpaRepository<EnergyUsageSummary, Long> {
    // 월간 총 사용량 합계
    @Query("SELECT COALESCE(SUM(e.actualKwh),0) " +
            "FROM EnergyUsageSummary e " +
            "WHERE e.periodType = :periodType " +
            "AND e.periodDate = :periodDate")
    Double sumActualKwhByPeriod(PeriodType periodType, LocalDate periodDate);

    Optional<EnergyUsageSummary> findTopByEnergyDeviceOrderByCreatedAtDesc(EnergyDevice device);
    Optional<EnergyUsageSummary> findTopByEnergyDeviceAndPeriodTypeOrderByCreatedAtDesc(
            EnergyDevice device, PeriodType periodType
    );

    Optional<EnergyUsageSummary> findByEnergyDeviceAndPeriodTypeAndPeriodDate(
            EnergyDevice device,
            PeriodType periodType,
            LocalDate periodDate
    );

    Optional<EnergyUsageSummary> findByEnergyDeviceAndPeriodTypeAndPeriodDateAndTimeSlot(
            EnergyDevice device,
            PeriodType periodType,
            LocalDate periodDate,
            Integer timeSlot
    );

    // 1) 디바이스 기준, periodDate 이전 N개
    @Query("""
        select eus.actualKwh
        from EnergyUsageSummary eus
        where eus.energyDevice.deviceId = :deviceId
          and eus.periodType = :periodType
          and (:timeSlot is null or eus.timeSlot = :timeSlot)
          and eus.periodDate < :periodDate
        order by eus.periodDate desc, eus.createdAt desc
    """)
    List<Double> findRecentActualKwhByDeviceBeforeDate(
            @Param("deviceId") Long deviceId,
            @Param("periodType") PeriodType periodType,
            @Param("timeSlot") Integer timeSlot,
            @Param("periodDate") LocalDate periodDate,
            Pageable pageable
    );

    default List<Double> findRecentActualKwhByDeviceBeforeDate(
            Long deviceId, PeriodType periodType, Integer timeSlot, LocalDate periodDate, int n
    ) {
        return findRecentActualKwhByDeviceBeforeDate(deviceId, periodType, timeSlot, periodDate, PageRequest.of(0, n));
    }

    // 2) 디바이스 타입 기준, periodDate 이전 N개
    @Query("""
        select eus.actualKwh
        from EnergyUsageSummary eus
        where eus.energyDevice.deviceType = :deviceType
          and eus.periodType = :periodType
          and (:timeSlot is null or eus.timeSlot = :timeSlot)
          and eus.periodDate < :periodDate
        order by eus.periodDate desc, eus.createdAt desc
    """)
    List<Double> findRecentActualKwhByDeviceTypeBeforeDate(
            @Param("deviceType") DeviceType deviceType,
            @Param("periodType") PeriodType periodType,
            @Param("timeSlot") Integer timeSlot,
            @Param("periodDate") LocalDate periodDate,
            Pageable pageable
    );

    default List<Double> findRecentActualKwhByDeviceTypeBeforeDate(
            DeviceType deviceType, PeriodType periodType, Integer timeSlot, LocalDate periodDate, int n
    ) {
        return findRecentActualKwhByDeviceTypeBeforeDate(deviceType, periodType, timeSlot, periodDate, PageRequest.of(0, n));
    }

    // 3) TIME_SLOT_AVG 전용: 타입 + timeSlot 기준, periodDate 이전 N개
    @Query("""
        select eus.actualKwh
        from EnergyUsageSummary eus
        where eus.energyDevice.deviceType = :deviceType
          and eus.periodType = com.jjld.domain.energy.entity.Enum.PeriodType.TIME_SLOT
          and eus.timeSlot = :timeSlot
          and eus.periodDate < :periodDate
        order by eus.periodDate desc, eus.createdAt desc
    """)
    List<Double> findRecentActualKwhByDeviceTypeAndTimeSlotBeforeDate(
            @Param("deviceType") DeviceType deviceType,
            @Param("timeSlot") Integer timeSlot,
            @Param("periodDate") LocalDate periodDate,
            Pageable pageable
    );

    default List<Double> findRecentActualKwhByDeviceTypeAndTimeSlotBeforeDate(
            DeviceType deviceType, Integer timeSlot, LocalDate periodDate, int n
    ) {return findRecentActualKwhByDeviceTypeAndTimeSlotBeforeDate(deviceType, timeSlot, periodDate, PageRequest.of(0, n));}

    // 그래프 찍기용 메소드!!!!!!!!!
    // =========================
    // Chart 1) TIME_SLOT 패턴: 특정 일자 0~23시 합계
    // =========================
    @Query("""
        select eus.timeSlot, coalesce(sum(eus.actualKwh), 0), coalesce(sum(eus.expectedKwh), 0)
        from EnergyUsageSummary eus
        where eus.periodType = com.jjld.domain.energy.entity.Enum.PeriodType.TIME_SLOT
          and eus.periodDate = :date
          and (:deviceId is null or eus.energyDevice.deviceId = :deviceId)
        group by eus.timeSlot
        order by eus.timeSlot asc
    """)
    List<Object[]> sumTimeSlotByDate(
            @Param("date") LocalDate date,
            @Param("deviceId") Long deviceId
    );

    // =========================
    // Chart 1) DAILY 패턴: 최근 N일 합계
    // =========================
    @Query("""
        select eus.periodDate, coalesce(sum(eus.actualKwh), 0), coalesce(sum(eus.expectedKwh), 0)
        from EnergyUsageSummary eus
        where eus.periodType = com.jjld.domain.energy.entity.Enum.PeriodType.DAILY
          and eus.periodDate between :startDate and :endDate
          and (:deviceId is null or eus.energyDevice.deviceId = :deviceId)
        group by eus.periodDate
        order by eus.periodDate asc
    """)
    List<Object[]> sumDailyBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("deviceId") Long deviceId
    );

    // =========================
    // Chart 1) MONTHLY 패턴: 최근 N개월 합계 (periodDate=월1일)
    // =========================
    @Query("""
        select eus.periodDate, coalesce(sum(eus.actualKwh), 0), coalesce(sum(eus.expectedKwh), 0)
        from EnergyUsageSummary eus
        where eus.periodType = com.jjld.domain.energy.entity.Enum.PeriodType.MONTHLY
          and eus.periodDate between :startMonth and :endMonth
          and (:deviceId is null or eus.energyDevice.deviceId = :deviceId)
        group by eus.periodDate
        order by eus.periodDate asc
    """)
    List<Object[]> sumMonthlyBetween(
            @Param("startMonth") LocalDate startMonth,
            @Param("endMonth") LocalDate endMonth,
            @Param("deviceId") Long deviceId
    );

    // =========================
    // Chart 2) deviceType 분포: MONTHLY 기준 유형별 actual 합계
    // =========================
    @Query("""
        select eus.energyDevice.deviceType, coalesce(sum(eus.actualKwh), 0)
        from EnergyUsageSummary eus
        where eus.periodType = com.jjld.domain.energy.entity.Enum.PeriodType.MONTHLY
          and eus.periodDate = :monthStart
          and (:deviceId is null or eus.energyDevice.deviceId = :deviceId)
        group by eus.energyDevice.deviceType
        order by sum(eus.actualKwh) desc
    """)
    List<Object[]> sumMonthlyByDeviceType(
            @Param("monthStart") LocalDate monthStart,
            @Param("deviceId") Long deviceId
    );
}
