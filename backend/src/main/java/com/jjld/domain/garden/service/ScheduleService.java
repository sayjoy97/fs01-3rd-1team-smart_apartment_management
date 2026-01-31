package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.ScheduleReq;
import com.jjld.domain.garden.dto.ScheduleRes;
import com.jjld.domain.garden.dto.ScheduleSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleService {
    void createSchedule(Long gardenId, ScheduleReq scheduleReq);

    Page<ScheduleRes> getSchedules(ScheduleSearchCondition cond, Pageable pageable);
}
