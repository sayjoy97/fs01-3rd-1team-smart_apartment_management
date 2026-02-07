package com.jjld.global.mqtt;

import com.jjld.global.mqtt.router.MqttRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttService {
    private final MqttPublish mqttPublish;
    private final MqttRouter mqttRouter;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            Object payloadObj = message.getPayload();
            String payload = payloadObj instanceof byte[]
                    ? new String((byte[]) payloadObj)
                    : payloadObj.toString();

            String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();

            log.info("받은 메시지: {}", payload);
            log.info("구독 토픽: {}", topic);

            mqttRouter.route(topic, payload);
        } catch (Exception e) {
            // MQTT 리스너는 절대 예외 밖으로 던지지 않음
            log.error("MQTT 메시지 처리 중 리스너 레벨 오류", e);
        }
    }
}
