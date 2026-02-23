package com.jjld.domain.parkingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedStat {
    private Long totalSum;
    private Double totalAvg;
}
