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
public class EnergyControlLogResponse {
    private Boolean beforeState;
    private Boolean afterState;
    private String adminName;
    private String reason;
    private LocalDateTime controlledAt;
}
