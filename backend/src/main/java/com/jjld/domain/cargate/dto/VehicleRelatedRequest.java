package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleRelatedRequest {

    @NotBlank
    private String plateNumber;

    @NotNull
    private VehicleType vehicleType;

    // 세대 등록용
    private String houseInfo;
    private String vehicleOwner;

    // 관리자 승인용
    private String approvalReason; // 택배, 방문, 업체 등
    private LocalDate startAt;      // null → 오늘
    private LocalDate endAt;        // null → 무기한
}
