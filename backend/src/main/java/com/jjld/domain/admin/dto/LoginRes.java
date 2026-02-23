package com.jjld.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class LoginRes {
    private String accessToken;
    private String refreshToken;
    private String username;
    private List<String> roles;
    private LoginAdminRes loginAdminRes;
}
