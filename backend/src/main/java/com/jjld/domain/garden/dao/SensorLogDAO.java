package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Device;
import com.jjld.domain.garden.entity.SensorLog;

import java.util.Optional;

public interface SensorLogDAO {
    Optional<SensorLog> getCurrentSensorLog(Device device);
}
