package com.jjld.domain.elevator.repository;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ElevatorEventLogRepository extends JpaRepository<ElevatorEventLog, Integer>, JpaSpecificationExecutor<ElevatorEventLog> {
    Page<ElevatorEventLog> findByElevator(Elevator elevator, Specification<ElevatorEventLog> spec, Pageable pageable);
}
