package com.jjld.domain.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyDashboardResponse {
    // 이번 달 총 사용량
    private Double monthlyUsageKwh;

    // 점검 필요 설비 수 (CHECK_REQUIRED)
    private Long checkRequiredCount;

    // 점검 중 설비 수 (CHECKING)
    private Long checkingCount;

    // 정상 설비 수 (NORMAL)
    private Long normalCount;

    // 예상 절감 가능 비용 (월간 추정)
    private Double possibleSavingCost;
}
