package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.ElevatorEventLog;
import com.jjld.domain.elevator.repository.ElevatorEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ElevatorEventLogDAOImpl implements ElevatorEventLogDAO {
    private final ElevatorEventLogRepository elevatorEventLogRepository;

    // elevatorId를 이용해 ElevatorEventLog 목록 조회
    @Override
    public Page<ElevatorEventLog> getLogs(Specification<ElevatorEventLog> spec, Pageable pageable) {
        return elevatorEventLogRepository.findAll(spec, pageable);
    }

    @Override
    public void save(ElevatorEventLog elevatorEventLog) {
        elevatorEventLogRepository.save(elevatorEventLog);
    }
}
