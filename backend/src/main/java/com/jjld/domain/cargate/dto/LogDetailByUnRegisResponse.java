package com.jjld.domain.cargate.dto;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class LogDetailByUnRegisResponse extends LogDetailBaseResponse{
    Integer calculatedFee;
}
