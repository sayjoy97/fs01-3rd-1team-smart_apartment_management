package com.jjld.domain.garden.repository;

import com.jjld.domain.garden.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> , JpaSpecificationExecutor<Schedule> {
}
