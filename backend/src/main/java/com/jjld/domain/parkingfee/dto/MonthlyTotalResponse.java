package com.jjld.domain.parkingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTotalResponse {
    private YearMonth month;
    private Long monthlySum;
    private Double monthlyAvg;
}
