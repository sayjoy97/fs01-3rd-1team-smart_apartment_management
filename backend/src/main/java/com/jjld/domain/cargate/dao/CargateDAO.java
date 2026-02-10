package com.jjld.domain.cargate.dao;

import com.jjld.domain.cargate.entity.Cargate;
import com.jjld.domain.cargate.entity.Enum.GateType;

public interface CargateDAO {

    // 번호로 게이트 찾기
    Cargate findByCargateId(Long cargateId);

    // 게이트 타입으로 게이트 찾기
    Cargate findByCargateType(GateType gateType);
}
