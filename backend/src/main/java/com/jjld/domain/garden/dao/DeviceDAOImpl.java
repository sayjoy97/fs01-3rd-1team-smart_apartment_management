package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceDAOImpl implements DeviceDAO {
    private final DeviceRepository deviceRepository;

    // gardenId를 이용해 디바이스 목록 조회
    @Override
    public List<Device> getDevices(Garden garden) {
        return deviceRepository.findAllByGarden(garden);
    }

    @Override
    public void createDevice(Device device) {
        deviceRepository.save(device);
    }
}
