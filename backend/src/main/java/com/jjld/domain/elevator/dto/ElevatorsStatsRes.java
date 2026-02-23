package com.jjld.domain.elevator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorsStatsRes {
    private long totalElevators;
    private long errorElevators;
    private long repairElevators;
}
