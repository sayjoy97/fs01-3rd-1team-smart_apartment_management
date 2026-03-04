package com.jjld.domain.cargate.dto;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class LogDetailByRegisResponse extends LogDetailBaseResponse{
    String vehicleOwner;
    String houseInfo;
}
