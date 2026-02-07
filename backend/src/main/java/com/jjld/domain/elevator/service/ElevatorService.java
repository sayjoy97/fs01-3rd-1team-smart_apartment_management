package com.jjld.domain.elevator.service;

import com.jjld.domain.elevator.dto.ElevatorReq;
import com.jjld.domain.elevator.dto.ElevatorRes;

import java.util.List;

public interface ElevatorService {
    void createElevator(Long adminId, ElevatorReq elevatorReq);

    List<ElevatorRes> getElevators();
}
