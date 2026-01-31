package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleService {
    void createSchedule(Long gardenId, ScheduleReq scheduleReq);

    Page<ScheduleFilterRes> getSchedules(ScheduleSearchCondition cond, Pageable pageable);

    ScheduleRes getSchedule(Long scheduleId);

    void updateSchedule(Long scheduleId, UpdateScheduleReq updateScheduleReq);
}
