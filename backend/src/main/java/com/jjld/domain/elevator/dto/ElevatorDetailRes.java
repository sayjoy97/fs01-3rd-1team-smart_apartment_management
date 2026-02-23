package com.jjld.domain.elevator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorDetailRes {
    private ElevatorRes elevatorRes;
    private Page<ElevatorEventLogRes> elevatorEventLogs;
}
