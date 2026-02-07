package com.jjld.domain.elevator.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.elevator.dao.ElevatorDAO;
import com.jjld.domain.elevator.dao.ElevatorEventLogDAO;
import com.jjld.domain.elevator.dto.ElevatorDetailRes;
import com.jjld.domain.elevator.dto.ElevatorEventLogRes;
import com.jjld.domain.elevator.dto.ElevatorReq;
import com.jjld.domain.elevator.dto.ElevatorRes;
import com.jjld.domain.elevator.entity.Elevator;
import com.jjld.domain.elevator.entity.ElevatorEventLog;
import com.jjld.domain.elevator.entity.Enum.Direction;
import com.jjld.domain.elevator.entity.Enum.DoorStatus;
import com.jjld.domain.elevator.entity.Enum.ElevatorState;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.ConflictException;
import com.jjld.global.exception.businessexceptions.ForbiddenException;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElevatorServiceImpl implements ElevatorService {
    private final ElevatorDAO elevatorDAO;
    private final AdminDAO adminDAO;
    private final ElevatorEventLogDAO elevatorEventLogDAO;
    private final ModelMapper modelMapper;

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
    public List<ElevatorRes> getElevators() {
        List<Elevator> elevators = elevatorDAO.getElevators();

        List<ElevatorRes> response = elevators
                .stream()
                .map(elevator -> {
                    ElevatorRes elevatorRes = new ElevatorRes(
                            elevator.getElevatorId(),
                            elevator.getDong(),
                            elevator.getHogi(),
                            elevator.getState()
                    );
                    return elevatorRes;
                })
                .collect(Collectors.toList());

        return response;
    }

    // 엘리베이터 상태 변경
    @Override
    public void updateElevatorState(Long elevatorId, ElevatorState elevatorState) {
        Elevator elevator = elevatorDAO.getElevator(elevatorId).orElse(null);

        if (elevator == null) {
            throw new NotFoundException(ErrorCode.ELEVATOR_NOT_FOUND, "엘리베이터를 찾을 수 없습니다.");
        }

        elevator.setState(elevatorState);

        elevatorDAO.save(elevator);
    }

    // 엘리베이터 삭제
    @Override
    public void deleteElevator(Long elevatorId, Long adminId) {
        Admin admin = adminDAO.getAdmin(adminId).orElse(null);

        if (admin == null) {
            throw new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "엘리베이터를 삭제할 관리자를 찾을 수 없습니다.");
        }

        if (admin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.SUPER_ADMIN_ONLY, "총 관리자만 접근할 수 있는 기능입니다.");
        }

        if (elevatorDAO.getElevator(elevatorId).isEmpty()) {
            throw new NotFoundException(ErrorCode.ELEVATOR_NOT_FOUND, "엘리베이터를 찾을 수 없습니다.");
        }

        elevatorDAO.deleteElevator(elevatorId);
    }

    // 엘리베이터 상세 조회
    @Override
    public ElevatorDetailRes getElevatorDetailInfo(Long elevatorId) {
        Elevator elevator = elevatorDAO.getElevator(elevatorId).orElse(null);

        if (elevator == null) {
            throw new NotFoundException(ErrorCode.ELEVATOR_NOT_FOUND, "엘리베이터를 찾을 수 없습니다.");
        }

        Pageable pageable = PageRequest.of(0, 5);  // 최근 5개만
        List<ElevatorEventLog> entityLogs = elevatorEventLogDAO.getLogs(elevator, pageable);

        ElevatorRes elevatorRes = modelMapper.map(elevator, ElevatorRes.class);

        List<ElevatorEventLogRes> dtoLogs = entityLogs
                .stream()
                .map(elevatorEventLog -> modelMapper.map(elevatorEventLog, ElevatorEventLogRes.class))
                .collect(Collectors.toList());

        ElevatorDetailRes response = new ElevatorDetailRes(elevatorRes, dtoLogs);

        return response;
    }
}
