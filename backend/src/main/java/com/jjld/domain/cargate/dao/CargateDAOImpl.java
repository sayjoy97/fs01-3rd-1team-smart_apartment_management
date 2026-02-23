package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.Cargate;
import com.jjld.domain.cargate.entity.Enum.GateType;
import com.jjld.domain.cargate.repository.CargateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CargateDAOImpl implements CargateDAO {
    private final CargateRepository cargateRepository;

    // 번호로 게이트 찾기
    @Override
    public Cargate findByCargateId(Long cargateId) {
        return cargateRepository.findById(cargateId)
                .orElseThrow(() -> new IllegalStateException("Not Found"));
    }

    // 게이트 타입으로 게이트 찾기
    @Override
    public Cargate findByCargateType(GateType gateType) {
        return cargateRepository.findByGateType(gateType);
    }
}
