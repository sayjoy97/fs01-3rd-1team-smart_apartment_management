package com.jjld.domain.garden.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.Enum.AdminRole;
import com.jjld.domain.garden.dao.DeviceDAO;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dao.SensorLogDAO;
import com.jjld.domain.garden.dto.GardenReq;
import com.jjld.domain.garden.dto.GardenRes;
import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.entity.SensorLog;
import com.jjld.global.exception.admin.AdminNotFoundException;
import com.jjld.global.exception.admin.SuperAdminOnlyException;
import com.jjld.global.exception.garden.GardenNotFoundException;
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
    private final AdminDAO adminDAO;
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

                    // 센서 미설치 시 값 처리
                    gardenRes.setCurrentTemperature("센서 미설치");
                    gardenRes.setCurrentHumidity("센서 미설치");
                    gardenRes.setCurrentSoilMoisture("센서 미설치");

                    // 설치된 센서는 값 처리
                    if (!devices.isEmpty()) {
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
                    }

                    return gardenRes;
                })
                .collect(Collectors.toList());

        return response;
    }

    // 정원 관리 구역 수정
    @Override
    public void updateGarden(Long gardenId, GardenReq gardenReq) {
        Garden garden = gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new GardenNotFoundException());

        garden.setName(gardenReq.getName());
        garden.setLocation(gardenReq.getLocation());
        garden.setAreaSize(gardenReq.getAreaSize());

        gardenDAO.updateGarden(garden);
    }

    @Override
    public void deleteGarden(Long gardenId, Long adminId) {
        Admin admin = adminDAO.getAdmin(adminId)
                .orElseThrow(() -> new AdminNotFoundException());

        if (admin.getAdminRole().equals(AdminRole.ADMIN)) {
            throw new SuperAdminOnlyException();
        }

        gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new GardenNotFoundException());

        gardenDAO.deleteGarden(gardenId);
    }

    @Override
    public void toggleWatering(Long gardenId) {
        Garden garden = gardenDAO.getGarden(gardenId)
            .orElseThrow(() -> new GardenNotFoundException());

        garden.setIsWatering(!garden.getIsWatering());
        gardenDAO.updateGarden(garden);
    }

    // getGardens에서 센서값을 처리할 때 활용하는 메서드
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
