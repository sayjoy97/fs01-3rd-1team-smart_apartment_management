package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.GardenRes;
import com.jjld.domain.garden.dto.ScheduleReq;
import com.jjld.domain.garden.dto.ScheduleFilterRes;
import com.jjld.domain.garden.dto.ScheduleSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleService {
    void createSchedule(Long gardenId, ScheduleReq scheduleReq);

    Page<ScheduleFilterRes> getSchedules(ScheduleSearchCondition cond, Pageable pageable);

    GardenRes getSchedule(Long scheduleId);
}
