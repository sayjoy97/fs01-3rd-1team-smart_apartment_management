package com.jjld.domain.elevator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementReq {
    private String advertisementTitle;
    private String advertisementContent;
    private LocalDate advertisementStartDate;
    private LocalDate advertisementEndDate;
}
