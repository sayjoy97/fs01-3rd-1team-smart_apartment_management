package com.jjld.domain.elevator.repository;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ElevatorEventLogRepository extends JpaRepository<ElevatorEventLog, Integer> {
    List<ElevatorEventLog> findByElevatorOrderByCreatedAtDesc(Elevator elevator, Pageable pageable);
}
