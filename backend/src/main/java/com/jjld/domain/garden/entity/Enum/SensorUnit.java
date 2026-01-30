package com.jjld.domain.garden.entity.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SensorUnit {
    CELSIUS("℃"),
    PERCENT("%"),
    LUX("lux");

    private final String unit;
}