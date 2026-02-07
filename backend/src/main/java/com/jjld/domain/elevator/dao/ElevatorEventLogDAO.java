package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ElevatorEventLogDAO {
    List<ElevatorEventLog> getLogs(Elevator elevator, Pageable pageable);

    void save(ElevatorEventLog elevatorEventLog);
}
