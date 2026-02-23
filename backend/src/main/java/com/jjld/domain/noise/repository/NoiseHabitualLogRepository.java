package com.jjld.domain.noise.repository;

import com.jjld.domain.noise.entity.NoiseHabitualLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoiseHabitualLogRepository extends JpaRepository<NoiseHabitualLog, Long> {
}
