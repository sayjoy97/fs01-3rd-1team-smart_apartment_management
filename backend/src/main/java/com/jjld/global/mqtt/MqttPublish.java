package com.jjld.global.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttPublish {

    void sendToMqtt(String payload); // 기본 토픽 사용

    void sandToMqtt(String payload, @Header(MqttHeaders.TOPIC) String topic);
}
