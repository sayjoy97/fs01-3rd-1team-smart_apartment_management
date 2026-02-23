package com.jjld.domain.energy.dto;

import com.jjld.domain.energy.entity.Enum.AnalysisStatus;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
