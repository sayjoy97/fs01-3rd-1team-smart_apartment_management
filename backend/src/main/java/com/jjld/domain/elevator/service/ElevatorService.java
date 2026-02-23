package com.jjld.domain.elevator.service;

import com.jjld.domain.admin.dto.DeleteReq;
import com.jjld.domain.elevator.dto.*;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ElevatorService {
    void createElevator(Long adminId, ElevatorReq elevatorReq);

    Page<ElevatorRes> getElevators(ElevatorSearchCondition cond, Pageable pageable);

    void updateElevatorState(Long elevatorId, ElevatorState state);

    void deleteElevator(Long elevatorId, Long adminId, DeleteReq deleteReq);

    ElevatorDetailRes getElevatorDetailInfo(Long elevatorId, ElevatorEventLogSearchCondition cond, Pageable pageable);

    void testMqtt(Long elevatorId, String payload);

    ElevatorsStatsRes getStats();
}
