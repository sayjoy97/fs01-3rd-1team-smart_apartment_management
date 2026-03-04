package com.jjld.domain.house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseSearchCond {
    private Integer houseDong;
    private Integer houseHo;
    private String householderName;
}
