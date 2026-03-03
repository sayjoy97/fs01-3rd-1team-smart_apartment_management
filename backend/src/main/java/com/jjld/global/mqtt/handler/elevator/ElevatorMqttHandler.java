package com.jjld.global.mqtt.handler.elevator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjld.domain.elevator.service.ElevatorEventLogService;
import com.jjld.domain.elevator.service.ElevatorService;
import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.mqtt.handler.MqttMessageHandler;
import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("elevatorMqttHandler")
@RequiredArgsConstructor
@Slf4j
public class ElevatorMqttHandler implements MqttMessageHandler {
    private final ElevatorService elevatorService;
    private final ElevatorEventLogService elevatorEventLogService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicInfo topicInfo, String payload) {
        int dong = Integer.parseInt(topicInfo.getFullTopicParts()[2]);
        int hogi = Integer.parseInt(topicInfo.getFullTopicParts()[3]);
        ElevatorServiceType serviceType = ElevatorServiceType.from(topicInfo.getFullTopicParts()[4]);

        try {
            switch (serviceType) {
                case STATUS -> log.info(payload);
                case EVENT -> elevatorEventLogService.createLog(dong, hogi, payload);
            }

        } catch (BusinessException e) {
            throw new BadRequestException(ErrorCode.INVALID_PAYLOAD_FORMAT, "MQTT 메시지 형식이 잘못되었습니다.");
        } catch (Exception e) {
            log.error("Elevator MQTT 처리 실패");
        }
    }

    @Override
    public MqttServiceType getServiceType() {
        return MqttServiceType.ELEVATOR;
    }
}


