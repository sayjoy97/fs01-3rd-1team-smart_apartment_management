package com.jjld.domain.entrancedoor.dto;

import com.jjld.domain.entrancedoor.entity.Enum.FrontDoorStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntranceGateResponse {
    private Long doorId;
    private Integer houseDong;
    private String status;
    private LocalDateTime lastAccessTime;
}
