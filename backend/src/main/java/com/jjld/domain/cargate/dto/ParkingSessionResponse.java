package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParkingSessionResponse {
    private Long parkingSessionId;
    private LocalDateTime entryAt;
    private LocalDateTime exitAt;
    private ParkingStatus status;
}
