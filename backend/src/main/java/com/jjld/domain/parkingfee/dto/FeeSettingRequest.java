package com.jjld.domain.parkingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeSettingRequest {
    private int baseTime;
    private int baseCharge;
    private int unitMinutes;
    private int unitCharge;
    private Boolean peakEnabled;
    private LocalTime peakStartAt;
    private LocalTime peakEndAt;
    private int peakUnitMinutes;
    private int peakUnitCharge;
}
