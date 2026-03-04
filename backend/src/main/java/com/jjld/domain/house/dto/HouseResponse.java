package com.jjld.domain.house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseResponse {
    private Long houseId;
    private Integer houseDong;
    private Integer houseHo;
    private String householderName;
    private String householderPhone;
    private LocalDate moveInAt;
    private boolean houseStatus;


}
