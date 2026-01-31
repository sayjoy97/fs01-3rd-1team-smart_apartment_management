package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EntryExitRecordResponse {
    private Long cargateEventId;
    private String plateNumber;
    private String parkingStatus;
    private VehicleType vehicleType;
    private LocalDateTime eventAt;
}
