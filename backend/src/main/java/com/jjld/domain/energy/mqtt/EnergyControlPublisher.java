package com.jjld.domain.energy.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjld.global.mqtt.MqttPublish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnergyControlPublisher {
    private final MqttPublish mqttPublish;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onControlCommitted(EnergyControlEvent event) {
        try {
            String topic = "jjld/energy/" + event.getDeviceId() + "/CONTROL";

            EnergyControlCommand cmd = EnergyControlCommand.builder()
                    .deviceId(event.getDeviceId())
                    .operate(event.getOperate())
                    .reason(event.getReason())
                    .requestId(UUID.randomUUID().toString())
                    .sentAt(LocalDateTime.now())
                    .build();

            String payload = objectMapper.writeValueAsString(cmd);

            mqttPublish.sendToMqtt(payload, topic);
            log.info("ENERGY CONTROL publish OK topic={}, payload={}", topic, payload);
        } catch (Exception e) {
            // 커밋은 끝났으니, 여기서 실패해도 서버가 죽으면 안 됨
            log.error("ENERGY CONTROL publish FAIL", e);
        }
    }
}
