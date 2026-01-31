package com.jjld.domain.garden.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.dto.AdminRes;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dao.ScheduleDAO;
import com.jjld.domain.garden.dto.*;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.entity.Schedule;
import com.jjld.domain.garden.specification.ScheduleSearchSpecification;
import com.jjld.global.exception.ForbiddenException;
import com.jjld.global.exception.NotFoundException;
import com.jjld.global.exception.admin.AdminNotFoundException;
import com.jjld.global.exception.garden.GardenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

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
    public Page<ScheduleFilterRes> getSchedules(ScheduleSearchCondition cond, Pageable pageable) {
        Specification<Schedule> spec = ScheduleSearchSpecification.withCondition(cond);
        Page<Schedule> Schedules = scheduleDAO.getSchedules(spec, pageable);
        Page<ScheduleFilterRes> response = Schedules
                .map(schedule -> {
                    ScheduleFilterRes scheduleFilterRes = modelMapper.map(schedule, ScheduleFilterRes.class);
                    scheduleFilterRes.setName(schedule.getGarden().getName());
                    scheduleFilterRes.setAdminName(schedule.getAdmin().getAdminName());

                    return scheduleFilterRes;
                });

        return response;
    }

    // 정원 관리 일정 조회
    @Override
    public ScheduleRes getSchedule(Long scheduleId) {
        Schedule schedule = scheduleDAO.getSchedule(scheduleId)
                .orElseThrow(() -> new NotFoundException("관리 일정을 찾을 수 없습니다."));

//        ScheduleRes response = modelMapper.map(schedule, ScheduleRes.class);
        ScheduleRes response = ScheduleRes.builder()
                .scheduleId(schedule.getScheduleId())
                .gardenRes(modelMapper.map(schedule.getGarden(), GardenRes.class))
                .adminRes(modelMapper.map(schedule.getAdmin(), AdminRes.class))
                .workTitle(schedule.getWorkTitle())
                .workContent(schedule.getWorkContent())
                .workStartDate(schedule.getWorkStartDate())
                .workEndDate(schedule.getWorkEndDate())
                .state(schedule.getState())
                .priority(schedule.getPriority())
                .build();


        return response;
    }

    // 정원 관리 일정 수정
    @Override
    public void updateSchedule(Long scheduleId, UpdateScheduleReq updateScheduleReq) {
        Schedule schedule = scheduleDAO.getSchedule(scheduleId)
                .orElseThrow(() -> new NotFoundException("관리 일정을 찾을 수 없습니다."));

        if (!updateScheduleReq.getAdminId().equals(schedule.getAdmin().getAdminId())) {
            throw new ForbiddenException("일정을 생성한 관리자만 수정할 수 있습니다.");
        }

        schedule.setWorkTitle(updateScheduleReq.getWorkTitle());
        schedule.setWorkContent(updateScheduleReq.getWorkContent());
        schedule.setWorkStartDate(updateScheduleReq.getWorkStartDate());
        schedule.setWorkEndDate(updateScheduleReq.getWorkEndDate());
        schedule.setState(updateScheduleReq.getState());
        schedule.setPriority(updateScheduleReq.getPriority());

        scheduleDAO.updateSchedule(schedule);
    }
}
