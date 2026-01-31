package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.DeviceReq;
import jakarta.validation.Valid;

import java.util.List;

public interface DeviceService {
    void createDevices(Long gardenId, List<DeviceReq> deviceReqs);
}
