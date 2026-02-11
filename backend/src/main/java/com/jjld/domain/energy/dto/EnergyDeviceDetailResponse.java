package com.jjld.domain.energy.dto;

import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyDeviceDetailResponse {
    private Long deviceId;
    private String deviceName;
    private String location;

    private String deviceType;        // 설비 유형 (조명 등)
    private String buildingName;      // 건물명 (104동 등)
    private String houseInfo;         // 층/호실

    private DeviceStatus deviceStatus;
    private LocalDate lastCheckDate;

    private Double estimatedWasteKwh;
    private Double overusePercent;
    private Double estimatedWasteCost;
    private Double monthChangeRate;

    private String causeEstimate;
    private String recommendedAction;
    private String expectedEffectMessage;

    private LocalDateTime analyzedAt;
}
