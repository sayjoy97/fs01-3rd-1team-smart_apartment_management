package com.jjld.domain.parkingfee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllInOneChargeViewResponse {
    private long todayCount; // 금일 누적금액
    private long thisMonthCount; // 이번달 누적금액
    private long thisYearCount; // 이번년도 누적금액
    private BigDecimal monthAverageCount; // 월평균
    private long dayTopCount; // 일일 최고금액
    private long unRegisAverageCount; // 월평균 방문차량(미등록)
}
