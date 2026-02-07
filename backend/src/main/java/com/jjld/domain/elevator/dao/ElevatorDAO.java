package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.dto.ElevatorRes;
import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.Dong;

import java.util.List;
import java.util.Optional;

public interface ElevatorDAO {
    Optional<Elevator> getElevator(Dong dong, Integer hogi);

    void save(Elevator elevator);

    List<Elevator> getElevators();
}
