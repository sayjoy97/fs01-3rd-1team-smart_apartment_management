package com.jjld.domain.garden.repository;

import com.jjld.domain.garden.entity.ActuatorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActuatorLogRepository extends JpaRepository<ActuatorLog, Long> {
}
