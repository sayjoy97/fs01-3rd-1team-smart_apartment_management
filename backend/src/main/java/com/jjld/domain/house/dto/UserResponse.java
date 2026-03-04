package com.jjld.domain.house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private Long houseId;
    private Integer houseDong;
    private Integer houseHo;
    private String householderName;
    private String householderPhone;
    private String householderEmail;
    private String role;
}
