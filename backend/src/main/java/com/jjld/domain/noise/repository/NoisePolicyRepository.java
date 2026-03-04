package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.NoisePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoisePolicyRepository extends JpaRepository<NoisePolicy, Long> {
    // 현재 활성중인 정책이 뭔지 조회
    // isActive = true 는 항상 1개만 존재!!
    Optional<NoisePolicy> findByIsActiveTrue();

    // 현재 활성 정책 존재 여부 체크 (정책 교체 시 사용)
    boolean existsByIsActiveTrue();

    boolean existsByPolicyName(String policyName);
}
