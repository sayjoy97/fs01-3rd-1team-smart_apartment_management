package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecordDetailResponse {
    private Long cargateEventId;
    private String plateNumber;
    private String parkingStatus;
    private LocalDateTime entryAt;
    private LocalDateTime exitAt;
    private ParkingStatus status;
    private String imagePath;
}
