package com.jjld.domain.house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseDetailResponse {
    private Long houseId;
    private Integer houseDong;
    private Integer houseHo;

    private String householderName;
    private String householderPhone;
    private String householderEmail;

    private String entrancePass;
    private LocalDate moveInAt;
    private int householdSize;

    private List<String> cardUid;
}
