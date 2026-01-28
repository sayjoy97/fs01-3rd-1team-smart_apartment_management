package com.jjld.domain.admin.dto;

import com.jjld.domain.admin.entity.Enum.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRes {
    private Long adminId;
    private String adminLoginId;
    private String adminPass;
    private String adminName;
    private String adminPhone;
    private String adminEmail;
    private Boolean state;
    private AdminRole adminRole;
    private LocalDateTime createdAt;
}
