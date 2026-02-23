package com.jjld.domain.admin.dto;

import com.jjld.domain.admin.entity.Enum.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAdminRes {
    private Long adminId;
    private Boolean isFirstLogin;
    private String accessToken;
    private AdminRole adminRole;
}
