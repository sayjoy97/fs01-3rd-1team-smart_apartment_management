package com.jjld.domain.garden.repository;

import com.jjld.domain.garden.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
