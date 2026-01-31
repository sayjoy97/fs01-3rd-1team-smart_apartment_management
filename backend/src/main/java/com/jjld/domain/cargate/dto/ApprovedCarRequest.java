package com.jjld.domain.cargate.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ApprovedCarRequest {
    private String approvalReason;
    private LocalDate startAt;
    private LocalDate endAt;
}
