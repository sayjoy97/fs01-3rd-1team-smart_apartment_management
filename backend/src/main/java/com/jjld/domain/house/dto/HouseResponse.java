package com.jjld.domain.house.dto;

import com.jjld.domain.house.entity.House;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
