package com.jjld.domain.parkingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStat {
    private Long monthlySum;
    private Double monthlyAvg;
}
