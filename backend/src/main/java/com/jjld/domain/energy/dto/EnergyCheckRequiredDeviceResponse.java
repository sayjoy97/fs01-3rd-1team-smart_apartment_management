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
public class EnergyCheckRequiredDeviceResponse {
    private Long analysisId;

    private Long deviceId;
    private String deviceName;
    private String location;
    private Boolean isOperating;

    private DeviceStatus deviceStatus;
    private AnalysisStatus analysisStatus;

    private Double estimatedWasteKwh;
    private Double overusePercent;
    private Double estimatedWasteCost;

    private String causeEstimate;

    private LocalDateTime analyzedAt;
}
