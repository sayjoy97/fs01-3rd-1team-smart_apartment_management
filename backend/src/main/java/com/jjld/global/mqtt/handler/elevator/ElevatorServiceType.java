package com.jjld.global.mqtt.handler.elevator;

import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;

public enum ElevatorServiceType {
    STATUS,
    EVENT;

    public static ElevatorServiceType from(String value) {
        try {
            return ElevatorServiceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "알 수 없는 Elevator 서비스 타입: " + value);
        }
    }
}
