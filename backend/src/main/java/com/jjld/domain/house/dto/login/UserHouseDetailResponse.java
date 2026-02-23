package com.jjld.domain.house.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHouseDetailResponse {
    private Integer houseDong;
    private Integer houseHo;
    private String householderName;
    private String householderPhone;
    private String householderEmail;
    private LocalDate moveInAt;
}
