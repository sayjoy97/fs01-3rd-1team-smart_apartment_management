package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.Dong;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ElevatorDAO {
    Optional<Elevator> getElevator(Dong dong, Integer hogi);

    void save(Elevator elevator);

    Page<Elevator> getElevators(Specification<Elevator> spec, Pageable pageable);

    Optional<Elevator> getElevator(Long elevatorId);

    void deleteElevator(Long elevatorId);

    long countTotalElevators();

    long countByState(ElevatorState state);
}
