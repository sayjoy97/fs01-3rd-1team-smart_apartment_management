package com.jjld.domain.garden.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GardenReq {
    private String name;

    @NotBlank(message = "위치 설정은 필수입니다.")
    private String location;

    private Float areaSize;
}
