package com.jjld.domain.energy.service;

import com.jjld.domain.energy.dao.EnergyPolicyDAO;
import com.jjld.domain.energy.dto.EnergyPolicyCreateRequest;
import com.jjld.domain.energy.dto.EnergyPolicyResponse;
import com.jjld.domain.energy.entity.EnergyPolicy;
import com.jjld.domain.energy.repository.EnergyPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnergyPolicyServiceImpl implements EnergyPolicyService {
    private final EnergyPolicyDAO energyPolicyDAO;
    private final EnergyPolicyRepository energyPolicyRepository;

    @Override
    public EnergyPolicyResponse getActivePolicy() {
        // active가 여러 개여도 최신 1개 반환
        EnergyPolicy policy = energyPolicyDAO.findActivePolicy()
                .orElseThrow(() -> new IllegalStateException("활성화된 에너지 정책이 없습니다."));
        return toResponse(policy);
    }

    @Override
    public Page<EnergyPolicyResponse> getPolicyHistory(Pageable pageable) {
        return energyPolicyDAO.findPolicyHistory(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public EnergyPolicyResponse createPolicy(EnergyPolicyCreateRequest request) {
        // 기존 활성 정책이 여러 개여도 전부 비활성화
        List<EnergyPolicy> actives = energyPolicyRepository.findAllByIsActiveTrue();
        for (EnergyPolicy p : actives) {
            p.setIsActive(false);
        }
        energyPolicyRepository.saveAll(actives);

        // 새 정책 생성 (항상 활성화)
        EnergyPolicy newPolicy = EnergyPolicy.builder()
                .sensitivityPercent(request.getSensitivityPercent())
                .warningPercent(request.getWarningPercent())
                .compareBase(request.getCompareBase())
                .idleStartTime(request.getIdleStartTime())
                .idleEndTime(request.getIdleEndTime())
                .repeatLimit(request.getRepeatLimit())
                .repeatWindowHours(request.getRepeatWindowHours())
                .ignoreSingleBreach(request.getIgnoreSingleBreach())
                .alertWarning(request.getAlertWarning())
                .alertCheck(request.getAlertCheck())
                .costPerKwh(request.getCostPerKwh())
                .wasteThresholdKwh(request.getWasteThresholdKwh())
                .isActive(true)
                .build();

        EnergyPolicy saved = energyPolicyRepository.save(newPolicy);
        return toResponse(saved);
    }
    private EnergyPolicyResponse toResponse(EnergyPolicy policy) {
        return EnergyPolicyResponse.builder()
                .policyId(policy.getPolicyId())
                .sensitivityPercent(policy.getSensitivityPercent())
                .warningPercent(policy.getWarningPercent())
                .compareBase(policy.getCompareBase())
                .idleStartTime(policy.getIdleStartTime())
                .idleEndTime(policy.getIdleEndTime())
                .repeatLimit(policy.getRepeatLimit())
                .repeatWindowHours(policy.getRepeatWindowHours())
                .ignoreSingleBreach(policy.getIgnoreSingleBreach())
                .alertWarning(policy.getAlertWarning())
                .alertCheck(policy.getAlertCheck())
                .costPerKwh(policy.getCostPerKwh())
                .wasteThresholdKwh(policy.getWasteThresholdKwh())
                .isActive(policy.getIsActive())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}
