package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.*;
import com.jjld.domain.energy.entity.Enum.DeviceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnergyDeviceService {
    Page<EnergyCheckRequiredDeviceResponse> getCheckRequiredDevices(Pageable pageable);

    void startCheck(Long deviceId);

    void completeCheck(Long deviceId);

    EnergyDeviceDetailResponse getDeviceDetail(Long deviceId);

    List<EnergyControlLogResponse> getControlLogs(Long deviceId);

    Page<EnergyDeviceRowResponse> getDeviceList(DeviceStatus status, Pageable pageable);

    // 설비 ON/OFF 제어
    void controlDevice(Long deviceId, Boolean operate, String reason);

    Page<EnergySavingResultResponse> getSavingResults(Long deviceId, Pageable pageable);
}
