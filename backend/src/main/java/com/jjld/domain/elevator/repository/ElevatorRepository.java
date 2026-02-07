package com.jjld.domain.elevator.repository;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.Dong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElevatorRepository extends JpaRepository<Elevator, Long> {
    Optional<Elevator> findByDongAndHogi(Dong dong, Integer hogi);
}
