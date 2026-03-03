package com.jjld.domain.elevator.service;

import com.jjld.domain.elevator.dao.ElevatorDAO;
import com.jjld.domain.elevator.dao.ElevatorEventLogDAO;
import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;
import com.jjld.domain.elevator.entity.Enum.Dong;
import com.jjld.domain.elevator.entity.Enum.ElevatorEventType;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElevatorEventLogServiceImpl implements ElevatorEventLogService {
    private final ElevatorDAO elevatorDAO;
    private final ElevatorEventLogDAO elevatorEventLogDAO;

    @Override
    public void createLog(int dongValue, int hogi, String payload) {
        Dong dong = Dong.fromDong(dongValue);
        Elevator elevator = elevatorDAO.getElevator(dong, hogi)
                .orElseThrow(() -> new ConflictException(ErrorCode.ELEVATOR_NOT_FOUND, "이벤트 로그를 저장할 엘리베이터를 찾을 수 없습니다."));

        String[] data = payload.split(":");
        ElevatorEventType eventType = ElevatorEventType.valueOf(data[0]);
        Integer floor = Integer.valueOf(data[1]);
        String message = data[2];
        ElevatorEventLog elevatorEventLog = ElevatorEventLog
                .builder()
                .elevator(elevator)
                .eventType(eventType)
                .floor(floor)
                .message(message)
                .build();

        elevatorEventLogDAO.save(elevatorEventLog);
    }
}
