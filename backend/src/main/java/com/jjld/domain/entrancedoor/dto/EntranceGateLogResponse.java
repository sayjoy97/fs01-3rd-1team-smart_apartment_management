package com.jjld.domain.entrancedoor.dto;

import com.jjld.domain.entrancedoor.entity.EntranceGateLog;
import com.jjld.domain.entrancedoor.entity.Enum.AccessType;
import com.jjld.domain.entrancedoor.entity.Enum.FailReason;
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

    private Integer houseDong;
    private Integer houseHo;
    private LocalDateTime accessedAt;
    private String accessType;
    private String adminName;
    private String failReason;

    public EntranceGateLogResponse(EntranceGateLog log, String adminName) {
        this.houseDong = log.getHouse().getHouseDong();
        this.houseHo = log.getHouse().getHouseHo();
        this.accessedAt = log.getAccessedAt();
        this.adminName = adminName;
        this.accessType = log.getAccessType().name();
        this.failReason = log.getFailReason().name();
    }
}
