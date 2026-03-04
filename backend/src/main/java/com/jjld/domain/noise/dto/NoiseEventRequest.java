package com.jjld.domain.noise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoiseEventRequest {
    private Integer soundLevel;
}