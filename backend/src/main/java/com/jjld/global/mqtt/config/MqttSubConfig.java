package com.jjld.global.mqtt.config;

import com.jjld.global.mqtt.MqttService;
import com.jjld.global.mqtt.topic.MqttTopicRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MqttSubConfig {

    private final MqttService mqttService;
    private final MqttTopicRegistry mqttTopicRegistry;

    @Value("${spring.mqtt.client-id}")
    private String clientId;

    @Bean
    public MqttClient mqttSubscriber(MqttPahoClientFactory clientFactory) throws MqttException {
        String subClientId = clientId + "_sub_" + UUID.randomUUID();

        log.info("Creating Direct MQTT Client");
        log.info("Client ID: {}", subClientId);

        // MqttClient 직접 생성
        MqttConnectOptions options = clientFactory.getConnectionOptions();
        MqttClient client = new MqttClient(
                options.getServerURIs()[0],
                subClientId,
                null
        );

        // 콜백 설정
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                log.error("MQTT 연결 끊김", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                log.info("메시지 도착 Topic: {}, Payload: {}", topic, new String(message.getPayload()));

                try {
                    // MqttService로 전달
                    Map<String, Object> headers = new HashMap<>();
                    headers.put("mqtt_receivedTopic", topic);
                    headers.put("mqtt_receivedQos", message.getQos());
                    headers.put("mqtt_receivedRetained", message.isRetained());

                    GenericMessage<String> springMessage = new GenericMessage<>(
                            new String(message.getPayload()),
                            headers
                    );

                    mqttService.handleMessage(springMessage);
                } catch (Exception e) {
                    log.error("메시지 처리 중 오류", e);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // 발행용 콜백 (여기서는 사용 안 함)
            }
        });

        // 연결
        client.connect(options);
        log.info("MQTT 연결 성공");

        // 구독
        mqttTopicRegistry.getAllTopics().forEach(topic -> {
            try {
                client.subscribe(topic, 1);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
            log.info("MQTT 구독 성공: {}", topic);
        });

        return client;
    }
}