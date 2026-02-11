package com.jjld.domain.energy.dao;

import com.jjld.domain.energy.entity.EnergyAnalysis;
import com.jjld.domain.energy.entity.Enum.AnalysisStatus;
import com.jjld.domain.energy.repository.EnergyAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EnergyDeviceDAOImpl implements EnergyDeviceDAO {
    private final EnergyAnalysisRepository energyAnalysisRepository;

    @Override
    public Page<EnergyAnalysis> findCheckRequiredDevices(Pageable pageable) {
        return energyAnalysisRepository.findLatestByStatus(AnalysisStatus.CHECK_REQUIRED, pageable);
    }
}
