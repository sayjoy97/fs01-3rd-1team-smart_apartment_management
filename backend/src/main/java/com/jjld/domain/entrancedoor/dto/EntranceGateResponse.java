package com.jjld.domain.entrancedoor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
