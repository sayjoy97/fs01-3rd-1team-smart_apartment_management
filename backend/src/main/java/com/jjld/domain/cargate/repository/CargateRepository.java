package com.jjld.domain.cargate.repository;

import com.jjld.domain.cargate.entity.CarGate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargateRepository extends JpaRepository<CarGate, Long> {
}
