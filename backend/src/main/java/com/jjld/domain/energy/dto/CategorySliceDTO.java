package com.jjld.domain.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySliceDTO {
    private String name;       // deviceType 문자열
    private double value;      // actual 합
    private double percentage; // 0~100
}
