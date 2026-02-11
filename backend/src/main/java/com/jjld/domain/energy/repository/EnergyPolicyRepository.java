package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnergyPolicyRepository extends JpaRepository<EnergyPolicy, Long> {
    Optional<EnergyPolicy> findByIsActiveTrue();
}
