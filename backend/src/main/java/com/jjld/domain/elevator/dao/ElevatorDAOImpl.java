package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.Enum.Dong;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import com.jjld.domain.elevator.repository.ElevatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<Elevator> getElevators(Specification<Elevator> spec, Pageable pageable) {
        return elevatorRepository.findAll(spec, pageable);
    }

    // elevatorId를 이용해서 엘리베이터 조회
    @Override
    public Optional<Elevator> getElevator(Long elevatorId) {
        return elevatorRepository.findById(elevatorId);
    }

    // 엘리베이터 삭제
    @Override
    public void deleteElevator(Long elevatorId) {
        elevatorRepository.deleteById(elevatorId);
    }

    @Override
    public long countTotalElevators() {
        return elevatorRepository.count();
    }

    @Override
    public long countByState(ElevatorState state) {
        return elevatorRepository.countByState(state);
    }

}
