package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleRegisterRequest {

    @NotBlank
    private String plateNumber;

    @NotNull
    private VehicleType vehicleType;

    @NotNull
    private int registerType; // 1: 세대 등록, 2: 관리자 승인

    // 세대 등록용
    private Long houseId;
    private String vehicleOwner;

    // 관리자 승인용
    private String approvalType;   // 택배, 방문, 업체 등
    private String approvalReason;
    private LocalDate startAt;      // null → 오늘
    private LocalDate endAt;        // null → 무기한
}
