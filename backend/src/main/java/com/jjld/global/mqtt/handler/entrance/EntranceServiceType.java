package com.jjld.global.mqtt.handler.entrance;

import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;

public enum EntranceServiceType {

    // 이 밑으로 복사 후 본인 파일에 붙여넣기

    // 토픽에서 지정한 서비스 타입을 이곳에서 관리
    CARD;

    //          [본인 기능]
    public static EntranceServiceType from(String value) {
        try {
            //   [본인 기능]
            return EntranceServiceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "알 수 없는 Entrance 서비스 타입: " + value);
            //                                                                     [본인 기능]
        }
    }
}
