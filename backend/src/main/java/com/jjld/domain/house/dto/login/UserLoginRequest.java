package com.jjld.domain.house.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    private Integer houseDong;
    private Integer houseHo;
    private String householderEmail;
    private String password;

    public UserLoginRequest(Integer houseHo, Integer houseDong) {
        this.houseHo = houseHo;
        this.houseDong = houseDong;
    }
}
