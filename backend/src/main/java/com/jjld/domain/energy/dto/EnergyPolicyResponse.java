package com.jjld.domain.energy.dto;

import com.jjld.domain.energy.entity.Enum.CompareBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyPolicyResponse {
    private Long policyId;

    private Integer sensitivityPercent;
    private Integer warningPercent;

    private CompareBase compareBase;

    private LocalTime idleStartTime;
    private LocalTime idleEndTime;

    private Integer repeatLimit;
    private Integer repeatWindowHours;

    private Boolean ignoreSingleBreach;

    private Boolean alertWarning;
    private Boolean alertCheck;

    private Integer costPerKwh;

    private Double wasteThresholdKwh;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
