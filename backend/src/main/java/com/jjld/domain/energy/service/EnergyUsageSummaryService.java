package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.EnergyUsageSummaryCreateRequest;

public interface EnergyUsageSummaryService {
    void createSummary(EnergyUsageSummaryCreateRequest request);
}
