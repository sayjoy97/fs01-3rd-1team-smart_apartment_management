package com.jjld.domain.admin.dto;

import com.jjld.domain.admin.entity.Enum.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchCondition {
    private String adminLoginId;
    private String adminName;
    private Boolean state;
    private AdminRole adminRole;
}
