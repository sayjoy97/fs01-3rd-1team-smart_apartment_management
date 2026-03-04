package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.ElevatorEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ElevatorEventLogDAO {
    Page<ElevatorEventLog> getLogs(Specification<ElevatorEventLog> spec, Pageable pageable);

    void save(ElevatorEventLog elevatorEventLog);
}
