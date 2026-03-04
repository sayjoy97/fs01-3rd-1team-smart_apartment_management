package com.jjld.domain.elevator.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dto.DeleteReq;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.elevator.dao.ElevatorDAO;
import com.jjld.domain.elevator.dao.ElevatorEventLogDAO;
import com.jjld.domain.elevator.dto.*;
import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;
import com.jjld.domain.elevator.entity.Enum.Direction;
import com.jjld.domain.elevator.entity.Enum.DoorStatus;
import com.jjld.domain.elevator.entity.Enum.ElevatorEventType;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import com.jjld.domain.elevator.specification.ElevatorEventLogSpecification;
import com.jjld.domain.elevator.specification.ElevatorSpecification;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.ConflictException;
import com.jjld.global.exception.businessexceptions.ForbiddenException;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import com.jjld.global.exception.businessexceptions.UnauthorizedException;
import com.jjld.global.mqtt.MqttPublish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElevatorServiceImpl implements ElevatorService {
    private final ElevatorDAO elevatorDAO;
    private final AdminDAO adminDAO;
    private final ElevatorEventLogDAO elevatorEventLogDAO;
    private final MqttPublish mqttPublish;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;

    // 엘리베이터 생성
    @Override
    public void createElevator(Long adminId, ElevatorReq elevatorReq) {
        Admin admin = adminDAO.getAdmin(adminId).orElse(null);

        if (admin == null) {
            throw new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "엘리베이터를 생성할 관리자를 찾을 수 없습니다.");
        }

        if (admin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        if (elevatorDAO.getElevator(elevatorReq.getDong(), elevatorReq.getHogi()).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_ELEVATOR, "엘리베이터가 이미 존재합니다.");
        }

        Elevator elevator = modelMapper.map(elevatorReq, Elevator.class);
        elevator.setCurrentFloor(1);
        elevator.setDirection(Direction.STOP);
        elevator.setDoorStatus(DoorStatus.CLOSED);
        elevator.setState(ElevatorState.IDLE);

        elevatorDAO.save(elevator);
    }

    // 엘리베이터 목록 조회
    @Override
    public Page<ElevatorRes> getElevators(ElevatorSearchCondition cond, Pageable pageable) {
        Specification<Elevator> spec = ElevatorSpecification.withCondition(cond);
        Page<Elevator> elevators = elevatorDAO.getElevators(spec, pageable);

        Page<ElevatorRes> response = elevators
                .map(elevator -> {
                    ElevatorRes elevatorRes = new ElevatorRes(
                            elevator.getElevatorId(),
                            elevator.getDong(),
                            elevator.getHogi(),
                            elevator.getState()
                    );
                    return elevatorRes;
                });

        return response;
    }

    // 엘리베이터 상태 변경
    @Override
    public void updateElevatorState(Long elevatorId, ElevatorState state) {
        Elevator elevator = elevatorDAO.getElevator(elevatorId).orElse(null);

        if (elevator == null) {
            throw new NotFoundException(ErrorCode.ELEVATOR_NOT_FOUND, "엘리베이터를 찾을 수 없습니다.");
        }

        elevator.setState(state);

        ElevatorEventType eventType = null;
        String message = null;

        switch (state) {
            case ERROR -> {
                eventType = ElevatorEventType.ERROR;
                message = "엘리베이터가 고장났습니다.";
            }
            case REPAIR -> {
                eventType = ElevatorEventType.REPAIR;
                message = "엘리베이터 점검을 시작합니다.";
            }
            case IDLE -> {
                eventType = ElevatorEventType.IDLE;
                message = "엘리베이터 수리가 완료됐습니다.";
            }
        }

        if (eventType == null) {
            throw new NotFoundException(ErrorCode.ELEVATOR_EVENT_TYPE_NOT_FOUND, "엘리베이터 이벤트 타입을 찾을 수 없습니다.");
        }

        ElevatorEventLog elevatorEventLog = ElevatorEventLog.builder()
                .elevator(elevator)
                .eventType(eventType)
                .message(message)
                .build();

        String topic = "jjld/command/elevator/" + elevator.getDong()  + "/" + elevator.getHogi() + "/status";
        String payload = "{\"status\":\"" + eventType.toString() + "\"}";
        mqttPublish.sendToMqtt(payload, topic);

        elevatorDAO.save(elevator);
        elevatorEventLogDAO.save(elevatorEventLog);
    }

    // 엘리베이터 삭제
    @Override
    public void deleteElevator(Long elevatorId, Long adminId, DeleteReq deleteReq) {
        Admin admin = adminDAO.getAdmin(adminId).orElse(null);

        if (admin == null) {
            throw new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "엘리베이터를 삭제할 관리자를 찾을 수 없습니다.");
        }

        if (admin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        if (!encoder.matches(deleteReq.getAdminPass(), admin.getAdminPass())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS, "비밀번호가 일치하지 않습니다.");
        }

        if (elevatorDAO.getElevator(elevatorId).isEmpty()) {
            throw new NotFoundException(ErrorCode.ELEVATOR_NOT_FOUND, "엘리베이터를 찾을 수 없습니다.");
        }

        elevatorDAO.deleteElevator(elevatorId);
    }

    // 엘리베이터 상세 조회
    @Override
    public ElevatorDetailRes getElevatorDetailInfo(Long elevatorId, ElevatorEventLogSearchCondition cond, Pageable pageable) {
        Elevator elevator = elevatorDAO.getElevator(elevatorId).orElse(null);

        if (elevator == null) {
            throw new NotFoundException(ErrorCode.ELEVATOR_NOT_FOUND, "엘리베이터를 찾을 수 없습니다.");
        }


        Specification<ElevatorEventLog> spec = ElevatorEventLogSpecification.withCondition(elevator, cond);

        Page<ElevatorEventLog> entityLogs = elevatorEventLogDAO.getLogs(spec, pageable);

        ElevatorRes elevatorRes = modelMapper.map(elevator, ElevatorRes.class);

        Page<ElevatorEventLogRes> dtoLogs = entityLogs
                .map(elevatorEventLog -> ElevatorEventLogRes
                        .builder()
                        .logId(elevatorEventLog.getLogId())
                        .elevatorId(elevatorEventLog.getElevator().getElevatorId())
                        .eventType(elevatorEventLog.getEventType())
                        .floor(elevatorEventLog.getFloor())
                        .message(elevatorEventLog.getMessage())
                        .createdAt(elevatorEventLog.getCreatedAt())
                        .build());

        ElevatorDetailRes response = new ElevatorDetailRes(elevatorRes, dtoLogs);

        return response;
    }

    @Override
    public void testMqtt(Long elevatorId, String payload) {
        log.info("elevatorId: {}", elevatorId);
        log.info("payload: {}", payload);
        mqttPublish.sendToMqtt("test", "jjld/command/elevator");
    }

    // 엘리베이터 통계 조회
    @Override
    public ElevatorsStatsRes getStats() {
        long totalElevators = elevatorDAO.countTotalElevators();
        long errorElevators = elevatorDAO.countByState(ElevatorState.ERROR);
        long repairElevators = elevatorDAO.countByState(ElevatorState.REPAIR);

        ElevatorsStatsRes response = new ElevatorsStatsRes(totalElevators, errorElevators, repairElevators);

        return response;
    }
}
