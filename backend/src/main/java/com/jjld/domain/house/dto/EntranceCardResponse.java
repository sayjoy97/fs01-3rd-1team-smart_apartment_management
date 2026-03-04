package com.jjld.domain.house.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntranceCardResponse {
    private Long cardId;
    private String cardUid;
    private String status;
    private Long houseId;


}
