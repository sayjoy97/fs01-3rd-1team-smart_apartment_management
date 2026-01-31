package com.jjld.domain.garden.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.admin.entity.History;
import com.jjld.domain.admin.specification.HistorySpecification;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dao.ScheduleDAO;
import com.jjld.domain.garden.dto.ScheduleReq;
import com.jjld.domain.garden.dto.ScheduleRes;
import com.jjld.domain.garden.dto.ScheduleSearchCondition;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.entity.Schedule;
import com.jjld.domain.garden.specification.ScheduleSearchSpecification;
import com.jjld.global.exception.admin.AdminNotFoundException;
import com.jjld.global.exception.garden.GardenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final GardenDAO gardenDAO;
    private final ScheduleDAO scheduleDAO;
    private final AdminDAO adminDAO;
    private final ModelMapper modelMapper;

    // 정원 관리 일정 생성
    @Override
    public void createSchedule(Long gardenId, ScheduleReq scheduleReq) {
        Garden garden = gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new GardenNotFoundException());

        Admin admin = adminDAO.getAdmin(scheduleReq.getAdminId())
                .orElseThrow(() -> new AdminNotFoundException());

        Schedule schedule = modelMapper.map(scheduleReq, Schedule.class);
        schedule.setGarden(garden);
        schedule.setAdmin(admin);
        schedule.setWorkEndDate(scheduleReq.getWorkEndDate());
        schedule.setWorkStartDate(scheduleReq.getWorkStartDate());

        System.out.println(schedule);

        scheduleDAO.createSchedule(schedule);
    }

    // 정원 관리 일정 필터 목록 조회
    @Override
    public Page<ScheduleRes> getSchedules(ScheduleSearchCondition cond, Pageable pageable) {
        Specification<Schedule> spec = ScheduleSearchSpecification.withCondition(cond);
        Page<Schedule> Schedules = scheduleDAO.getSchedules(spec, pageable);
        Page<ScheduleRes> response = Schedules
                .map(schedule -> {
                    ScheduleRes scheduleRes = modelMapper.map(schedule, ScheduleRes.class);
                    scheduleRes.setName(schedule.getGarden().getName());
                    scheduleRes.setAdminName(schedule.getAdmin().getAdminName());

                    return scheduleRes;
                });

        return response;
    }
}
