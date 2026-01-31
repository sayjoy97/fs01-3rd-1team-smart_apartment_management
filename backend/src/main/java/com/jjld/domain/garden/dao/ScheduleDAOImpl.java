package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Schedule;
import com.jjld.domain.garden.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScheduleDAOImpl implements ScheduleDAO {
    private final ScheduleRepository scheduleRepository;

    @Override
    public void createSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    @Override
    public Page<Schedule> getSchedules(Specification<Schedule> spec, Pageable pageable) {
        return scheduleRepository.findAll(spec, pageable);
    }
}
