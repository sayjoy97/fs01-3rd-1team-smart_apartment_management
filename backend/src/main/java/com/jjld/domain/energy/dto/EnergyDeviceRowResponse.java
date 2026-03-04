package com.jjld.domain.energy.dto;

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
public class EnergyDeviceRowResponse {
    private Long deviceId;
    private String deviceName;
    private String location;

    private Boolean isOperating;          // ON / OFF
    private DeviceStatus deviceStatus;    // 점검권장 / 점검중 / 정상

    private Double estimatedWasteKwh;     // 예상 낭비량
    private Double overusePercent;        // 현재 낭비 비율 (%)
    private Double estimatedWasteCost;    // 예상 절감액

    private Double monthChangeRate;       // 전월 대비 변화율

    private String causeEstimate;         // 원인 추정

    private LocalDateTime analyzedAt;     // 최신 분석 시각
}
