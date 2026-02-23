package com.jjld.domain.elevator.dto;

import com.jjld.domain.elevator.entity.Enum.Dong;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElevatorSearchCondition {
    private Dong dong;
    private Integer hogi;
    private String state;
}
