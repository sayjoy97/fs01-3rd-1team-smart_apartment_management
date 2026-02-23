package com.jjld.domain.elevator.repository;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.Dong;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElevatorRepository extends JpaRepository<Elevator, Long>, JpaSpecificationExecutor<Elevator> {
    Optional<Elevator> findByDongAndHogi(Dong dong, Integer hogi);

    long countByState(ElevatorState state);
}
