package com.jjld.global.mqtt.router;

import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.mqtt.Util.MqttTopicParser;
import com.jjld.global.mqtt.handler.MqttMessageHandler;
import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MqttRouter {

    private final List<MqttMessageHandler> handlers;
    private Map<MqttServiceType, MqttMessageHandler> handlerMap;

    @PostConstruct
    public void init() {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        MqttMessageHandler::getServiceType,
                        Function.identity()
                ));
    }

    public void route(String topic, String payload) {
        TopicInfo topicInfo = MqttTopicParser.parse(topic);

        MqttMessageHandler handler = handlerMap.get(topicInfo.getServiceType());
        if (handler == null) {
            throw new BadRequestException(ErrorCode.UNKNOWN_SERVICE_TYPE, "알 수 없는 MQTT 서비스 타입입니다.");
        }

        handler.handle(topicInfo, payload);
    }
}
