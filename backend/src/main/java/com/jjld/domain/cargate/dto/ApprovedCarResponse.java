package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.CurrentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovedCarResponse {
    private Long id;
    private String plateNumber;
    private CurrentStatus currentStatus;
    private LocalDateTime craetedAt;
}
