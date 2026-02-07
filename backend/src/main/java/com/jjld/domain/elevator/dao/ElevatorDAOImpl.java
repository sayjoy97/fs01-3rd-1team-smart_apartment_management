package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.Dong;
import com.jjld.domain.elevator.repository.ElevatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ElevatorDAOImpl implements ElevatorDAO {
    private final ElevatorRepository elevatorRepository;

    //  hogi를 이용해서 엘리베이터 조회
    @Override
    public Optional<Elevator> getElevator(Dong dong, Integer hogi) {
        return elevatorRepository.findByDongAndHogi(dong, hogi);
    }

    @Override
    public void save(Elevator elevator) {
        elevatorRepository.save(elevator);
    }
}
