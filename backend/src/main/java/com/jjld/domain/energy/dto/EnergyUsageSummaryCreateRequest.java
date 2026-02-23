package com.jjld.domain.energy.dto;

import com.jjld.domain.energy.entity.Enum.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyUsageSummaryCreateRequest {
    private Long deviceId;
    private PeriodType periodType;
    private LocalDate periodDate;
    private Integer timeSlot; // TIME_SLOT일 때만 사용 (그 외 null)
    private Double actualKwh;
    private Double expectedKwh;
}
