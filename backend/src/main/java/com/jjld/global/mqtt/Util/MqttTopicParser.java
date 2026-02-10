package com.jjld.global.mqtt.Util;

import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;
import org.springframework.stereotype.Component;

@Component
public class MqttTopicParser {
    public static TopicInfo parse(String topic) {
        if (topic == null || topic.isBlank()) {
            throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "토픽이 비어있음");
        }

        String[] parts = topic.split("/");

        if (parts.length < 3) {
            throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "토픽 길이 부족: " + topic);
        }

        try {
//            MqttServiceType serviceType = MqttServiceType.fromTopic(topic);
            MqttServiceType serviceType = MqttServiceType.from(parts[1]);
            return new TopicInfo(serviceType, parts);
        } catch (BusinessException e) {
            throw new BadRequestException(ErrorCode.INVALID_TOPIC_FORMAT, "알 수 없는 서비스 타입: " + parts[1]);
        }
    }
}
