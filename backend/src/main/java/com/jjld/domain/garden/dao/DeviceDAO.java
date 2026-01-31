package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Garden;

import java.util.List;
import java.util.Optional;

public interface DeviceDAO {
    List<Device> getDevices(Garden garden);

    void saveDevice(Device device);

    Optional<Device> getDevice(Long deviceId);
}
