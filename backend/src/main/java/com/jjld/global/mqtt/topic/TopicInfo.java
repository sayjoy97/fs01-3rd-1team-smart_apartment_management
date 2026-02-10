package com.jjld.global.mqtt.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicInfo {
    private final MqttServiceType serviceType;
    private final String[] fullTopicParts;
}
