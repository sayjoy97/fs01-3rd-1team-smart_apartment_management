package com.jjld.domain.complex.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplexRes {
    private Long id;
    private String name;
    private int totalHouseholds;
    private String address;
    private String phoneNumber;
    private String email;
    private LocalDate completionDate;
    private int buildingCount;
    private int maxFloor;
    private int parkingCapacity;
}
