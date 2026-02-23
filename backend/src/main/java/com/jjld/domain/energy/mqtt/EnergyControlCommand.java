package com.jjld.domain.energy.mqtt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyControlCommand {
    private Long deviceId;
    private Boolean operate;     // true=ON, false=OFF
    private String reason;
    private String requestId;    // UUID
    private LocalDateTime sentAt;
}
