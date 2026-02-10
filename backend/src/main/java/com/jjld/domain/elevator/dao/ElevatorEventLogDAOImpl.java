package com.jjld.domain.elevator.dao;

import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;
import com.jjld.domain.elevator.repository.ElevatorEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ElevatorEventLogDAOImpl implements ElevatorEventLogDAO {
    private final ElevatorEventLogRepository elevatorEventLogRepository;

    // elevatorId를 이용해 ElevatorEventLog 목록 조회
    @Override
    public List<ElevatorEventLog> getLogs(Elevator elevator, Pageable pageable) {
        return elevatorEventLogRepository.findByElevatorOrderByCreatedAtDesc(elevator, pageable);
    }

    @Override
    public void save(ElevatorEventLog elevatorEventLog) {
        elevatorEventLogRepository.save(elevatorEventLog);
    }
}
