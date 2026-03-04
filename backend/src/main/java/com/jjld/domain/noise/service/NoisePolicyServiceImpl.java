package com.jjld.domain.noise.service;

import com.jjld.domain.noise.entity.NoisePolicy;
import com.jjld.domain.noise.repository.NoisePolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NoisePolicyServiceImpl implements NoisePolicyService {
    private final NoisePolicyRepository noisePolicyRepository;
    // 새로운 소음 정책 생성 및 즉시 적용
    @Override
    public void createNoisePolicy(NoisePolicy noisePolicy) {
// 1. 기존 활성 정책이 있으면 비활성화
        noisePolicyRepository.findByIsActiveTrue()
                .ifPresent(activePolicy -> {
                    activePolicy.setIsActive(false);
                });
        // 2. 신규 정책 활성화
        noisePolicy.setIsActive(true);
        // 3) policyName 유니크 보장 (중복이면 자동으로 바꿔서 저장)
        noisePolicy.setPolicyName(ensureUniquePolicyName(noisePolicy.getPolicyName()));
        // 3. 정책 저장
        noisePolicyRepository.save(noisePolicy);
    }
    private String ensureUniquePolicyName(String baseName) {
        String name = (baseName == null ? "" : baseName.trim());
        if (name.isEmpty()) name = "정책";

        // 중복 없으면 그대로
        if (!noisePolicyRepository.existsByPolicyName(name)) return name;

        // ✅ 중복이면 timestamp suffix (절대 안 겹치게)
        String suffix = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String candidate = name + "_" + suffix;

        // 혹시나 같은 초에 중복 들어오면 한번 더 보정
        int i = 1;
        while (noisePolicyRepository.existsByPolicyName(candidate)) {
            candidate = name + "_" + suffix + "_" + i++;
        }
        return candidate;
    }
    // 현재 적용 중인 소음 정책 조회
    @Override
    @Transactional(readOnly = true)
    public NoisePolicy findActiveNoisePolicy() {
        return noisePolicyRepository.findByIsActiveTrue()
                .orElseThrow(() ->
                        new IllegalStateException("현재 활성화된 소음 정책이 없습니다.")
                );
    }
    // 특정 시간이 주간인지 판단
    @Override
    @Transactional(readOnly = true)
    public boolean isDayTime(LocalTime time) {
        NoisePolicy activePolicy = findActiveNoisePolicy();
        return !time.isBefore(activePolicy.getDayStartTime())
                && time.isBefore(activePolicy.getNightStartTime());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDayTimeNow() {
        return isDayTime(LocalTime.now());
    }
}
