package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.Cargate;
import com.jjld.domain.cargate.entity.Enum.GateType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargateRepository extends JpaRepository<Cargate, Long> {

    Cargate findByGateType(GateType gateType);
}
