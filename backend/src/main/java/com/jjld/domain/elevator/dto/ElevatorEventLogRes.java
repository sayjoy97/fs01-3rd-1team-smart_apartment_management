package com.jjld.domain.elevator.dto;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.ElevatorEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElevatorEventLogRes {
    private Long logId;
    private Long elevatorId;
    private ElevatorEventType eventType;
    private Integer floor;
    private String message;
    private LocalDateTime createdAt;
}
