package com.jjld.domain.entrancedoor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntranceDoorPayload {
        private String cardUid;
        private String entrancePass;
        private String adminCall;
}
