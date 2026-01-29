package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.ParkingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSessionResponse {
    private LocalDateTime entryAt;
    private LocalDateTime exitAt;
    private ParkingStatus status;
}
