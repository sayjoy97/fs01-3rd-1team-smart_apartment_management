package com.jjld.domain.energy.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnergyControlEvent {
    private Long deviceId;
    private Boolean operate;
    private String reason;
}
