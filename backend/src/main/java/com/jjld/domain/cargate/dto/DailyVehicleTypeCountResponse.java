package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyVehicleTypeCountResponse {
    private LocalDate date;
    private Map<VehicleType, Long> counts;
}
