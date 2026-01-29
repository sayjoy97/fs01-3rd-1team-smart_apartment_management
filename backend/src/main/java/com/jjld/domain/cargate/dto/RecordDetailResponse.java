package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
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
public class RecordDetailResponse {
    private Long cargateEventId;
    private String plateNumber;
    private String parkingStatus;
    private LocalDateTime entryAt;
    private LocalDateTime exitAt;
    private ParkingStatus status;
    private String imagePath;
}
