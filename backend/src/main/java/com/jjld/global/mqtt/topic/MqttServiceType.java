package com.jjld.global.mqtt.topic;

import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;

import java.util.Arrays;

public enum MqttServiceType {
    GARDEN,
    ELEVATOR,
    ENTRANCE;

    public static MqttServiceType from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.UNKNOWN_SERVICE_TYPE, "알 수 없는 MQTT 서비스 타입입니다."));
    }

//    public static MqttServiceType fromTopic(String topic) {
//        if (topic.contains("/garden/")) return GARDEN;
//        if (topic.contains("/elevator/")) return ELEVATOR;
//        throw new BadRequestException(ErrorCode.UNKNOWN_SERVICE_TYPE, "알 수 없는 MQTT 서비스 타입입니다." + topic);
//    }
}
