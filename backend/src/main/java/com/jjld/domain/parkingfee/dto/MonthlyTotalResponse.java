package com.jjld.domain.parkingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTotalResponse {
    private String month;
    private Long monthlySum;
    private Double monthlyAvg;
}
