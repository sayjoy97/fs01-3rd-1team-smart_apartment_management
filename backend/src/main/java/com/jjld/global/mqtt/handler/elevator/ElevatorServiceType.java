package com.jjld.global.mqtt.handler.elevator;

import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;

public enum ElevatorServiceType {
    // 토픽에서 지정한 서비스 타입을 이곳에서 관리
    TEST,
    WATER;

    //          [본인 기능]
    public static ElevatorServiceType from(String value) {
        try {
            //   [본인 기능]
            return ElevatorServiceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "알 수 없는 Elevator 서비스 타입: " + value);
            //                                                                     [본인 기능]
        }
    }
}
