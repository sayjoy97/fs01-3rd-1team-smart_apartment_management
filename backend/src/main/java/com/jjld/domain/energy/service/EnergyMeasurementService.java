package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.EnergyMeasurementCreateRequest;

public interface EnergyMeasurementService {
    void createMeasurement(EnergyMeasurementCreateRequest request);
}
