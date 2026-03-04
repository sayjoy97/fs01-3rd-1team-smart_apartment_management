package com.jjld.domain.entrancedoor.dto;

import com.jjld.domain.entrancedoor.entity.EntranceGateLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntranceGateLogResponse {
    private Long accessLogId;
    private Integer houseDong;
    private Integer houseHo;
    private LocalDateTime accessedAt;
    private String accessType;
    private String adminName;
    private String failReason;

    public EntranceGateLogResponse(EntranceGateLog log, String adminName) {
        this.accessLogId = log.getAccessLogId();
        this.houseDong = log.getHouseDong();
        this.houseHo = log.getHouseHo();
        this.accessedAt = log.getAccessedAt();
        this.adminName = adminName;
        this.accessType = log.getAccessType().name();
        this.failReason = log.getFailReason().name();
    }
}
