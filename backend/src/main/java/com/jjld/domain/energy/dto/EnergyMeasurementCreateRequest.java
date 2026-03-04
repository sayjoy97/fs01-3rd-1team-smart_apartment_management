package com.jjld.domain.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyMeasurementCreateRequest {
    private Long deviceId;
    private Double voltage;
    private Double current;
    private Double power;
    private Double energyKwh; // 누적값
}
