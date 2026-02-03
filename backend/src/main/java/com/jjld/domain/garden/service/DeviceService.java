package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.DeviceReq;
import com.jjld.domain.garden.entity.Enum.DeviceState;

import java.util.List;

public interface DeviceService {
    void createDevices(Long gardenId, List<DeviceReq> deviceReqs);

    void updateDevice(Long deviceId, DeviceState deviceState);

    void manualWatering(Long gardenId);
}
