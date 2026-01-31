package com.jjld.domain.cargate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovedCarRequest {
    private String approvalReason;
    private LocalDate startAt;
    private LocalDate endAt;
}
