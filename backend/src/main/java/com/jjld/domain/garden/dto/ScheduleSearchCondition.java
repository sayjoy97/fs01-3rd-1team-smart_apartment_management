package com.jjld.domain.garden.dto;

import com.jjld.domain.garden.entity.Enum.Priority;
import com.jjld.domain.garden.entity.Enum.ScheduleState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSearchCondition {
    private String name;
    private String adminName;
    private String workTitle;
    private LocalDate workStartDate;
    private LocalDate workEndDate;
    private ScheduleState state;
    private Priority priority;
}
