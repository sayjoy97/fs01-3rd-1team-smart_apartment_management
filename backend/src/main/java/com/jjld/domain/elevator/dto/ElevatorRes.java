package com.jjld.domain.elevator.dto;

import com.jjld.domain.elevator.entity.Enum.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorRes {
    private Long elevatorId;
    private Dong dong;
    private Integer hogi;
    private ElevatorState state;
}
