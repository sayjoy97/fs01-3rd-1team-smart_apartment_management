package com.jjld.domain.elevator.dto;

import com.jjld.domain.elevator.entity.Enum.*;
import com.jjld.domain.house.entity.House;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorRes {
    private Long elevatorId;
    private Dong dong;
    private Integer hogi;
    private House house;
    private CallMethod callMethod;
    private Integer currentFloor;
    private Integer targetFloor;
    private Direction direction;
    private ElevatorState state;
    private DoorStatus doorStatus;
    private LocalDateTime updatedAt;

    public ElevatorRes(Long elevatorId, Dong dong, Integer hogi, ElevatorState state) {
        this.elevatorId = elevatorId;
        this.dong = dong;
        this.hogi = hogi;
        this.state = state;
    }

}
