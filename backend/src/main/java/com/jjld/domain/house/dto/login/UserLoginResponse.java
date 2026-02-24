package com.jjld.domain.house.dto.login;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String accessToken;
    private String username;
    private String role;
    private boolean firstLogin;

}
