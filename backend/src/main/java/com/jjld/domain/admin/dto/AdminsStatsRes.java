package com.jjld.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminsStatsRes {
    private long totalAdmins;
    private long activeAdmins;
    private long newAdmins;
}
