package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dao.DeviceDAO;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dao.SensorLogDAO;
import com.jjld.domain.garden.dto.GardenReq;
import com.jjld.domain.garden.dto.GardenRes;
import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Enum.DeviceState;
import com.jjld.domain.garden.entity.Enum.DeviceType;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.entity.SensorLog;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GardenServiceImpl implements GardenService {
    private final GardenDAO gardenDAO;
    private final DeviceDAO deviceDAO;
    private final SensorLogDAO sensorLogDAO;
    private final ModelMapper modelMapper;

    // 정원 관리 구역 생성
    @Override
    public void createGarden(GardenReq gardenReq) {
        if (gardenReq.getName().equals("") || gardenReq.getName() == null) {
            gardenReq.setName(gardenReq.getLocation());
        }
        Garden garden = modelMapper.map(gardenReq, Garden.class);
        gardenDAO.createGarden(garden);
    }

    // 정원 관리 구역 목록 조회
    @Override
    public List<GardenRes> getGardens() {
        List<Garden> Gardens = gardenDAO.getGardens();
        List<GardenRes> response = Gardens
                .stream()
                .map(Garden -> {
                    GardenRes gardenRes = modelMapper.map(Garden, GardenRes.class);
                    List<Device> devices = deviceDAO.getDevices(Garden);

                    if (devices.isEmpty()) {  // 설치된 센서가 없을 때
                        gardenRes.setCurrentTemperature("센서 미설치");
                        gardenRes.setCurrentHumidity("센서 미설치");
                        gardenRes.setCurrentSoilMoisture("센서 미설치");
                    }

                    for(Device device : devices) {
                        switch (device.getDeviceType()) {
                            case TEMP:
                                gardenRes.setCurrentTemperature(setSensorLogValue(device));
                                break;
                            case HUMIDITY:
                                gardenRes.setCurrentHumidity(setSensorLogValue(device));
                                break;
                            case SOIL_MOISTURE:
                                gardenRes.setCurrentSoilMoisture(setSensorLogValue(device));
                                break;
                        }
                    }

                    return gardenRes;
                })
                .collect(Collectors.toList());

        return response;
    }


    // getGardens에서 센서값을 넣을 때 활용하는 메서드
    String setSensorLogValue (Device device) {
        switch (device.getState()) {
            case ERROR:
                return "센서 고장";
            case REPAIR:
                return "센서 수리 중";
            case NORMAL:
                SensorLog sensorLog = sensorLogDAO.getCurrentSensorLog(device).orElse(null);
                if (sensorLog == null) {
                    return "측정 중";
                } else {
                    return sensorLog.getValue().toString() + sensorLog.getUnit().getUnit();
                }
            default: return "-";
        }
    }
}
