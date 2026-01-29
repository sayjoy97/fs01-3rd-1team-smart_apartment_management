package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.NoiseEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoiseEventSensorRepository extends JpaRepository<NoiseEvent, Long> {
}
