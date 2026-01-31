package com.jjld.domain.garden.dao;

import com.jjld.domain.garden.entity.Schedule;
import com.jjld.domain.garden.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleDAOImpl implements ScheduleDAO {
    private final ScheduleRepository scheduleRepository;

    // 정원 관리 일정 생성
    @Override
    public void createSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    // 정원 관리 일정 필터 목록 조회
    @Override
    public Page<Schedule> getSchedules(Specification<Schedule> spec, Pageable pageable) {
        return scheduleRepository.findAll(spec, pageable);
    }

    // 정원 관리 일정 조회
    @Override
    public Optional<Schedule> getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    // 정원 관리 일정 수정
    @Override
    public void updateSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }
}
