package com.jjld.domain.noise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

// 정책 조회응답
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoisePolicyResponse {
    private Long policyId;
    private String policyName;

    private LocalTime dayStartTime;
    private LocalTime nightStartTime;

    private Integer soundLimit;
    private Integer repeatLimit;
    private Integer timeThreshold;

    private Boolean isActive;
    private LocalDateTime createdAt;
}
