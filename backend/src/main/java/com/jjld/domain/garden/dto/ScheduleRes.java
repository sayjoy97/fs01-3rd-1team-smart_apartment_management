package com.jjld.domain.garden.dto;

import com.jjld.domain.admin.dto.AdminRes;
import com.jjld.domain.admin.entity.Admin;
import com.jjld.domain.garden.entity.Enum.Priority;
import com.jjld.domain.garden.entity.Enum.ScheduleState;
import com.jjld.domain.garden.entity.Garden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRes {
    private Long scheduleId;
    private GardenRes gardenRes;
    private AdminRes adminRes;
    private String workTitle;
    private String workContent;
    private LocalDate workStartDate;
    private LocalDate workEndDate;
    private ScheduleState state = ScheduleState.SCHEDULED;
    private Priority priority;
}
