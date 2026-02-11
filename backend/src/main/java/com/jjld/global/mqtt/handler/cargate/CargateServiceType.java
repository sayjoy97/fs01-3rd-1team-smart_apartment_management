package com.jjld.global.mqtt.handler.cargate;

import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;

public enum CargateServiceType {
    ENTRY,
    EXIT,
    PAYMENT;

    public static CargateServiceType fromThird(String value){
        switch (value){
            case "entry":
            case "exit":
            case "payment":
                return CargateServiceType.valueOf(value.toUpperCase());
            default:
                throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "알 수 없는 Cargate 서비스 타입 " +  value);
        }
    }
}
