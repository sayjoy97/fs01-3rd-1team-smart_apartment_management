package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.CurrentStatus;
import com.jjld.domain.cargate.entity.Enum.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 관리자 승인차량 상세정보 조회 Response
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovedCarDetailResponse {
    private Long id;
    private String plateNumber;
    private VehicleType vehicleType;
    private CurrentStatus currentStatus;
    List<ParkingSessionResponse> parkingSessions;
    private String approvalReason;
    private LocalDateTime createdAt;
    private LocalDate startAt;
    private LocalDate endAt;
}
