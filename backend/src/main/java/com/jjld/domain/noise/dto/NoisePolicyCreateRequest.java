package com.jjld.domain.noise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

// 관리자 화면에서 설정 가능한 값만 받음
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoisePolicyCreateRequest {
    // 정책 이름(중복 불가)
    private String policyName;

    // 주간 시작 시각
    private LocalTime dayStartTime;

    // 야간 시작 시각
    private LocalTime nightStartTime;

    // 소음 강도 기준(dB)
    private Integer soundLimit;

    // 반복 허용 횟수 기준
    private Integer repeatLimit;

    // 반복 판단 시간 창(분)
    private Integer timeThreshold;
}
