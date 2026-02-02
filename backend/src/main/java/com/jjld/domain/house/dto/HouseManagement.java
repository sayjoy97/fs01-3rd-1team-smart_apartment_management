package com.jjld.domain.house.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseManagement {
    private Long houseId;
    private Integer houseDong;
    private Integer houseHo;
    private String householderName;
    private String householderPhone;
    private String householderEmail;
    private String HouseServiceImpl;
    private LocalDate moveInAt;
//    private int HouseholdSize;

    private List<Long> cardId;
    private List<Long> vehicleId;
}
