package com.jjld.domain.admin.dto;

import com.jjld.domain.admin.entity.Enum.AccessType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryRes {
    private Long historyId;
    private Long adminId;
    private String ipAddress;
    private AccessType accessType;
    private Boolean success;
    private String message;
    private LocalDateTime createdAt;
}
