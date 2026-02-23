package com.jjld.domain.energy.dao;

import com.jjld.domain.energy.entity.EnergyAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnergyDeviceDAO {
    Page<EnergyAnalysis> findCheckRequiredDevices(Pageable pageable);
}
