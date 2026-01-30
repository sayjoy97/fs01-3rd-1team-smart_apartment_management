package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegisteredCarResponse {
    private Long id;
    private String plateNumber;
    private VehicleType vehicleType;
    private LocalDateTime createdAt;
}
