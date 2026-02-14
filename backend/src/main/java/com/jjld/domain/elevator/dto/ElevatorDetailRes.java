package com.jjld.domain.elevator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorDetailRes {
    private ElevatorRes elevatorRes;
    private List<ElevatorEventLogRes> elevatorEventLogs;
}
