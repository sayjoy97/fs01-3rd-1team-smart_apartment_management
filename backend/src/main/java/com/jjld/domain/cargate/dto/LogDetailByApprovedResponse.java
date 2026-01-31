package com.jjld.domain.cargate.dto;

import com.jjld.domain.cargate.entity.Enum.CurrentStatus;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class LogDetailByApprovedResponse extends LogDetailBaseResponse{
    String approvalReason;
    LocalDateTime createdAt;
    CurrentStatus currentStatus;
}
