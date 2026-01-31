package com.jjld.domain.garden.service;

import com.jjld.domain.admin.dao.AdminDAO;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.garden.dao.GardenDAO;
import com.jjld.domain.garden.dao.ScheduleDAO;
import com.jjld.domain.garden.dto.ScheduleReq;
import com.jjld.domain.garden.entity.Garden;
import com.jjld.domain.garden.entity.Schedule;
import com.jjld.global.exception.admin.AdminNotFoundException;
import com.jjld.global.exception.garden.GardenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final GardenDAO gardenDAO;
    private final ScheduleDAO scheduleDAO;
    private final AdminDAO adminDAO;
    private final ModelMapper modelMapper;

    @Override
    public void createSchedule(Long gardenId, ScheduleReq scheduleReq) {
        Garden garden = gardenDAO.getGarden(gardenId)
                .orElseThrow(() -> new GardenNotFoundException());

        Admin admin = adminDAO.getAdmin(scheduleReq.getAdminId())
                .orElseThrow(() -> new AdminNotFoundException());

        Schedule schedule = modelMapper.map(scheduleReq, Schedule.class);
        schedule.setGarden(garden);
        schedule.setAdmin(admin);
        schedule.setWorkEndDate(scheduleReq.getWorkEndDate().atStartOfDay());
        schedule.setWorkStartDate(scheduleReq.getWorkStartDate().atStartOfDay());

        System.out.println(schedule);

        scheduleDAO.createSchedule(schedule);
    }
}
