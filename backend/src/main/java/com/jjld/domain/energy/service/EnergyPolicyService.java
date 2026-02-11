package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dto.EnergyPolicyCreateRequest;
import com.jjld.domain.energy.dto.EnergyPolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnergyPolicyService {
    EnergyPolicyResponse getActivePolicy();

    Page<EnergyPolicyResponse> getPolicyHistory(Pageable pageable);

    EnergyPolicyResponse createPolicy(EnergyPolicyCreateRequest request);
}
