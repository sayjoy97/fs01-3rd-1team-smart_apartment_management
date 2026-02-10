package com.jjld.domain.garden.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.garden.dao.ActuatorLogDAO;
import com.jjld.domain.garden.dao.DeviceDAO;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dto.DeviceReq;
import com.jjld.domain.garden.entity.ActuatorLog;
import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Enum.ActionType;
import com.jjld.domain.garden.entity.Enum.ControlType;
import com.jjld.domain.garden.entity.Enum.DeviceState;
import com.jjld.domain.garden.entity.Enum.DeviceType;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.ConflictException;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceDAO deviceDAO;
    private final GardenDAO gardenDAO;
    private final AdminDAO adminDAO;
    private final ActuatorLogDAO actuatorLogDAO;

    // 정원 관리 기능 디바이스 등록
    @Override
    public void createDevices(Long gardenId, List<DeviceReq> deviceReqs) {
        Garden garden = gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GARDEN_NOT_FOUND, "디바이스를 등록할 정원을 찾을 수 없습니다."));

        deviceReqs.forEach(deviceReq -> {
            Device device = Device.builder()
                    .garden(garden)
                    .deviceType(deviceReq.getDeviceType())
                    .state(deviceReq.getState())
                    .build();

            deviceDAO.saveDevice(device);
        });
    }

    // 정원 관리 기능 디바이스 상태 수정
    @Override
    public void updateDevice(Long deviceId, DeviceState deviceState) {
        Device device = deviceDAO.getDevice(deviceId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DEVICE_NOT_FOUND, "디바이스를 찾을 수 없습니다."));

        device.setState(deviceState);

        deviceDAO.saveDevice(device);
    }

    // 정원 관리 기능 수동 물주기
    @Override
    public void manualWatering(Long gardenId, Long adminId) {
        Garden garden = gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GARDEN_NOT_FOUND, "수동 물주기를 할 정원을 찾을 수 없습니다."));

        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ADMIN_NOT_FOUND, "수동 물주기를 할 관리자를 찾을 수 없습니다."));

        DeviceType deviceType = DeviceType.WATER_PUMP;
        Device device = deviceDAO.getDevice(garden, deviceType)
                .orElseThrow(() -> new NotFoundException(ErrorCode.DEVICE_NOT_FOUND, "등록된 워터 펌프를 찾을 수 없습니다."));

        if (device.getState().equals(DeviceState.ERROR)) {
            throw new ConflictException(ErrorCode.DEVICE_UNAVAILABLE, "워터 펌프 고장으로 물주기를 할 수 없습니다.");
        } else if (device.getState().equals(DeviceState.REPAIR)) {
            throw new ConflictException(ErrorCode.DEVICE_UNAVAILABLE, "워터 펌프 수리중. 물주기를 할 수 없습니다.");
        }

        // mqtt 통신을 통해 워터 펌프를 작동 시키는 메서드 호출
        // 통신 실패 시, 물주기 실패 시 등의 오류 처리 예정

        ActuatorLog actuatorLog = ActuatorLog.builder()
                .device(device)
                .admin(admin)
                .controlType(ControlType.MANUAL)
                .action(ActionType.ON)
                .reason("")
                .build();

        actuatorLogDAO.saveActuatorLog(actuatorLog);
    }
}
