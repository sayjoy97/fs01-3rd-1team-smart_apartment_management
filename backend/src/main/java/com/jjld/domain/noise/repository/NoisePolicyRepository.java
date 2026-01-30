package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.NoisePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoisePolicyRepository extends JpaRepository<NoisePolicy, Long> {
    // 현재 활성중인 정책이 뭔지 조회
    // isActive = true 는 항상 1개만 존재!!
    //
    Optional<NoisePolicy> findByIsActiveTrue();
}
