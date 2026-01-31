package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Garden;

import java.util.List;

public interface DeviceDAO {
    List<Device> getDevices(Garden garden);

    void createDevice(Device device);
}
