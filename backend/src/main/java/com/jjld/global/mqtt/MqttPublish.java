package com.jjld.global.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttClientFactory")
public interface MqttPublish {
    void sandToMqtt(String payload, @Header(MqttHeaders.TOPIC) String topic);
}
