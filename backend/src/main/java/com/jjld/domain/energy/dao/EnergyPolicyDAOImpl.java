package com.jjld.domain.energy.dao;

import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.repository.EnergyPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnergyPolicyDAOImpl implements EnergyPolicyDAO {
    private final EnergyPolicyRepository energyPolicyRepository;

    @Override
    public Optional<EnergyPolicy> findActivePolicy() {
        return energyPolicyRepository.findByIsActiveTrue();
    }

    @Override
    public Page<EnergyPolicy> findPolicyHistory(Pageable pageable) {
        return energyPolicyRepository.findAll(pageable);
    }
}
