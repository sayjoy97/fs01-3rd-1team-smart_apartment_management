package com.jjld.domain.noise.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoiseEventRequest {
    private Integer soundLevel;
}