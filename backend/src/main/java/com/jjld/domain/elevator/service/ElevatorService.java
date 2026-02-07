package com.jjld.domain.elevator.service;

import com.jjld.domain.elevator.dto.ElevatorReq;

public interface ElevatorService {
    void createElevator(Long adminId, ElevatorReq elevatorReq);
}
