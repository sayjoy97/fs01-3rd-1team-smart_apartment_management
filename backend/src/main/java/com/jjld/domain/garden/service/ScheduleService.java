package com.jjld.domain.garden.service;

import com.jjld.domain.garden.dto.ScheduleReq;

public interface ScheduleService {
    void createSchedule(Long gardenId, ScheduleReq scheduleReq);
}
