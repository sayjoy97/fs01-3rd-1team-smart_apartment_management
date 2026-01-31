package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dao.DeviceDAO;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dto.DeviceReq;
import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceDAO deviceDAO;
    private final GardenDAO gardenDAO;

    @Override
    public void createDevices(Long gardenId, List<DeviceReq> deviceReqs) {
        Garden garden = gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new NotFoundException("정원을 찾을 수 없습니다."));

        deviceReqs.forEach(deviceReq -> {
            Device device = Device.builder()
                    .garden(garden)
                    .deviceType(deviceReq.getDeviceType())
                    .state(deviceReq.getState())
                    .build();

            deviceDAO.createDevice(device);
        });
    }
}
