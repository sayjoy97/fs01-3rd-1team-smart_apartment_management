package com.jjld.domain.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergySavingResultResponse {
    private Long savingId;

    private Double beforeKwh;
    private Double afterKwh;

    private Double savedKwh;
    private Double savedCost;

    private LocalDateTime evaluatedAt;
}
