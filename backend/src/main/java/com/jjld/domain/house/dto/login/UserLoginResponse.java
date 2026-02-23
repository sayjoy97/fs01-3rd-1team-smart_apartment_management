package com.jjld.domain.house.dto.login;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String accessToken;
    private String username;
    private String role;
    private boolean firstLogin;
}
