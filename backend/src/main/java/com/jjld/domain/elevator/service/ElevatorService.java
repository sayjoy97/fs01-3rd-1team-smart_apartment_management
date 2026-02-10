package com.jjld.domain.elevator.service;

import com.jjld.domain.elevator.dto.ElevatorDetailRes;
import com.jjld.domain.elevator.dto.ElevatorReq;
import com.jjld.domain.elevator.dto.ElevatorRes;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;

import java.util.List;

public interface ElevatorService {
    void createElevator(Long adminId, ElevatorReq elevatorReq);

    List<ElevatorRes> getElevators();

    void updateElevatorState(Long elevatorId, ElevatorState elevatorState);

    void deleteElevator(Long elevatorId, Long adminId);

    ElevatorDetailRes getElevatorDetailInfo(Long elevatorId);

    void testMqtt(Long elevatorId, String payload);
}
