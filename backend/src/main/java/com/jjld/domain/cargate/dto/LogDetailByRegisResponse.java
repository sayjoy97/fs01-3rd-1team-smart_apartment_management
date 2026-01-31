package com.jjld.domain.cargate.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class LogDetailByRegisResponse extends LogDetailBaseResponse{
    Long houseId;
    String vehicleOwner;
}
