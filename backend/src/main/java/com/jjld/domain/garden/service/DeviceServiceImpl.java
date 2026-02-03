package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dao.DeviceDAO;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dto.DeviceReq;
import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Enum.DeviceState;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceDAO deviceDAO;
    private final GardenDAO gardenDAO;

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
    public void manualWatering(Long gardenId) {
        gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.GARDEN_NOT_FOUND, "수동 물주기를 할 정원을 찾을 수 없습니다."));

        // mqtt 통신을 통해 워터 펌프를 작동 시키는 메서드 호출
        // 통신 실패 시, 물주기 실패 시 등의 오류 처리 예정

    }
}
