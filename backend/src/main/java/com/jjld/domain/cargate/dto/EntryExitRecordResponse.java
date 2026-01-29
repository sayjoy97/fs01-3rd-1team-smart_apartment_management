package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntryExitRecordResponse {
    private Long cargateEventId;
    private String plateNumber;
    private String parkingStatus;
    private VehicleType vehicleType;
    private LocalDateTime eventAt;
}
