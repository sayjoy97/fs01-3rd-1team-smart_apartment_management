package com.jjld.global.mqtt.handler;

import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;

public interface MqttMessageHandler {
    void handle(TopicInfo topicInfo, String payload);

    MqttServiceType getServiceType();
}
