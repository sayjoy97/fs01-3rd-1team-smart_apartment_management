package com.jjld.domain.entrancedoor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntranceGateLogSearchCond {
    private Integer houseDong;
    private String accessType;
}
