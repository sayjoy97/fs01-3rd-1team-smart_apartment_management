package com.jjld.domain.noise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoiseDashboardResponse {
    // 오늘 발생한 소음 이벤트 수
    private long todayEventCount;

    // 오늘 정책위반의심(policyBreak=true) 이벤트 수
    private long todayPolicyBreakCount;

    // 승인대기(PENDING) 이벤트 수
    private long pendingEventCount;

    // 현재 시간대(주/야)
    private String currentTimeZone;
}
