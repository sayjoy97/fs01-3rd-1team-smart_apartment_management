package com.jjld.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassReq {
    private String adminLoginId;
    private String newPassword;
    private String confirmNewPassword;
}
