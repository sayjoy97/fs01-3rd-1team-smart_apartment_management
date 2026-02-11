package com.jjld.domain.energy.dao;

import com.jjld.domain.energy.entity.EnergyPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EnergyPolicyDAO {
    Optional<EnergyPolicy> findActivePolicy();

    Page<EnergyPolicy> findPolicyHistory(Pageable pageable);
}
