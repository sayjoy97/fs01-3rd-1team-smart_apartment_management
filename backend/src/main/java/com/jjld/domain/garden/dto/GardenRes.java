package com.jjld.domain.garden.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GardenRes {
    private Long gardenId;
    private String name;
    private String location;
    private Float areaSize;
    private Boolean isWatering;
    private LocalDateTime createdAt;
    private String currentHumidity;
    private String currentTemperature;
    private String currentSoilMoisture;
}
