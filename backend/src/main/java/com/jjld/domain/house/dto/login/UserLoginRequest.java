package com.jjld.domain.house.dto.login;

import lombok.*;

@Data
@Getter
@Setter
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
