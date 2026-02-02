package com.jjld.domain.parkingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleRateResponse {
    private long todayRate;
    private long thisMonthRate;
}
