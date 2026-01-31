package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.NoiseSensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoiseSensorRepository extends JpaRepository<NoiseSensor, Long> {
}
