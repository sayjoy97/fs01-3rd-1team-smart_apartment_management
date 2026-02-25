package com.jjld.global.mqtt.handler.noise;

import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;

public enum NoiseServiceType {
    // 토픽에서 지정한 서비스 타입을 이곳에서 관리
    EVENT;  // jjld/noise/{sensorId}/EVENT

    //          [본인 기능]
    public static NoiseServiceType from(String value) {
        try {
            //   [본인 기능]
            return NoiseServiceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "알 수 없는 Noise 서비스 타입: " + value);
            //                                                                     [본인 기능]
        }
    }
}
