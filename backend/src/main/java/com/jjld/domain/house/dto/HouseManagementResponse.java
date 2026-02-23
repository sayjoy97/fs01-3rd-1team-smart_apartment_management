package com.jjld.domain.house.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseManagementResponse {
    private Long houseId;
    private String householderName;
    private String householderPhone;
    private String householderEmail;
    private String entrancePass;
    private LocalDate moveInAt;
    private int householdSize;
    private Boolean houseStatus;

    private List<String> cardUid;

}
