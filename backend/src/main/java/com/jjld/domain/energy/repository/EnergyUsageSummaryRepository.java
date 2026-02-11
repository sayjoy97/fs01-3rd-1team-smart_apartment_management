package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyDevice;
import com.jjld.domain.energy.entity.EnergyUsageSummary;
import com.jjld.domain.energy.entity.Enum.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    Optional<EnergyUsageSummary> findByEnergyDeviceAndPeriodTypeAndPeriodDate(
            EnergyDevice device,
            PeriodType periodType,
            LocalDate periodDate
    );
}
