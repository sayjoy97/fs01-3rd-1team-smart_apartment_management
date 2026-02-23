package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.NoiseHabitualZone;
import com.jjld.domain.noise.entity.NoiseSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoiseHabitualZoneRepository extends JpaRepository<NoiseHabitualZone, Long> {
    boolean existsBySensorAndStatus(NoiseSensor sensor, String status);

    Optional<NoiseHabitualZone> findBySensorAndStatus(NoiseSensor sensor, String status);

    long countByStatus(String status);

    Page<NoiseHabitualZone> findByStatus(String status, Pageable pageable);
}
