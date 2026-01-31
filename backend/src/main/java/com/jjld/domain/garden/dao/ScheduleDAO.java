package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface ScheduleDAO {
    void createSchedule(Schedule schedule);

    Page<Schedule> getSchedules(Specification<Schedule> spec, Pageable pageable);

    Optional<Schedule> getSchedule(Long scheduleId);

    void updateSchedule(Schedule schedule);
}
