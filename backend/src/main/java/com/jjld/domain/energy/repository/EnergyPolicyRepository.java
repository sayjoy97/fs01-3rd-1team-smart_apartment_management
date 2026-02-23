package com.jjld.domain.energy.repository;

import com.jjld.domain.energy.entity.EnergyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyPolicyRepository extends JpaRepository<EnergyPolicy, Long> {
    // ✅ active 전부 가져오기(생성 시 전부 끄기용)
    List<EnergyPolicy> findAllByIsActiveTrue();
    // active가 여러 개여도 "최신 1개"만 가져오기
    Optional<EnergyPolicy> findTopByIsActiveTrueOrderByCreatedAtDesc();
}
