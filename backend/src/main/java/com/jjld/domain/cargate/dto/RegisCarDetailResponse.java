package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisCarDetailResponse {
    private Long id;
    private String plateNumber;
    private VehicleType vehicleType;
    List<ParkingSessionResponse> parkingSessions;
    private String vehicleOwner;
    private int hounsDong;
    private int houseHo;
    private LocalDateTime createdAt;
}
