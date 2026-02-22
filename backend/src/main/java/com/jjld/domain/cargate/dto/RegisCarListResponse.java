package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegisCarListResponse {
    private Long id;
    private String plateNumber;
    private String vehicleOwner;
    private int houseDong;
    private int houseHo;
    private VehicleType vehicleType;
    private LocalDateTime createdAt;
}
