package com.jjld.domain.cargate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordDetailResponse {
    private Long cargateEventId;
    private String plateNumber;
    private String parkingStatus;
    private List<ParkingSessionResponse> parkingSessions;
}
