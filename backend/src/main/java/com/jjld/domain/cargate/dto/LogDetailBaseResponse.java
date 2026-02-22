package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class LogDetailBaseResponse {
    Long cargateEventId;
    String plateNumber;
    Long vehicleId;
    ParkingStatus parkingStatus;
    LocalDateTime entryAt;
    LocalDateTime exitAt;
    long stayMinutes;
    VehicleType vehicleType;
}
