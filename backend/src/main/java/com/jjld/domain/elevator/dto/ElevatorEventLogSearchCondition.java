package com.jjld.domain.elevator.dto;

import com.jjld.domain.elevator.entity.Enum.ElevatorEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorEventLogSearchCondition {
    private ElevatorEventType eventType;
}
