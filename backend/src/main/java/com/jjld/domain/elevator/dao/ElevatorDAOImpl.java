package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.Dong;
import com.jjld.domain.elevator.repository.ElevatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ElevatorDAOImpl implements ElevatorDAO {
    private final ElevatorRepository elevatorRepository;

    // dong과 hogi를 이용해서 엘리베이터 조회
    @Override
    public Optional<Elevator> getElevator(Dong dong, Integer hogi) {
        return elevatorRepository.findByDongAndHogi(dong, hogi);
    }

    // 엘리베이터 생성
    @Override
    public void save(Elevator elevator) {
        elevatorRepository.save(elevator);
    }

    // 엘리베이터 목록 조회
    @Override
    public List<Elevator> getElevators() {
        return elevatorRepository.findAll();
    }

    // elevatorId를 이용해서 엘리베이터 조회
    @Override
    public Optional<Elevator> getElevator(Long elevatorId) {
        return elevatorRepository.findById(elevatorId);
    }
}
