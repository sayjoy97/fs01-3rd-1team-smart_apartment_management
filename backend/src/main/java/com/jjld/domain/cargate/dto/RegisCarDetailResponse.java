package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// 세대 등록차량 상세정보 조회 Response
@Data
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
