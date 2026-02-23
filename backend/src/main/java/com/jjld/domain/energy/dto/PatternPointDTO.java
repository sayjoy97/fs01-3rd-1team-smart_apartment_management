package com.jjld.domain.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatternPointDTO {
    private String label;
    private double actual;
    private double expected;
}
