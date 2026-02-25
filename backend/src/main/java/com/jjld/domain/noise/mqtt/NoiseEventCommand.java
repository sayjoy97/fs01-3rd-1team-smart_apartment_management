package com.jjld.domain.noise.mqtt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoiseEventCommand {
    private Long sensorId;
    private Integer soundLevel;
}
