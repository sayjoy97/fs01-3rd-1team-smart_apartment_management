package com.jjld.global.mqtt.handler.elevator;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ElevatorService elevatorService;  // ------ 수정할 부분 ------
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicInfo topicInfo, String payload) {
        Long elevatorId = Long.parseLong(topicInfo.getFullTopicParts()[2]);  // ------ 수정할 부분 ------
        ElevatorServiceType serviceType = ElevatorServiceType.from(topicInfo.getFullTopicParts()[3]);  // ------ 수정할 부분 ------
        // 토픽을 파싱해서 사용
        // garden의 경우 jjld/garden/[gardenId]/serviceType 으로 설정함
        // topicInfo.getFullTopicParts()[i] i를 설정해서 원하는 값을 파싱

        try {
            switch (serviceType) {  // 이곳에서 해당하는 gardenService의 메서드를 호출
                case TEST -> elevatorService.testMqtt(elevatorId, payload);  // ------ 수정할 부분 ------
                case WATER -> log.info("{} | WATER | {}", elevatorId, payload);  // ------ 수정할 부분 ------
            }

        } catch (BusinessException e) {
            throw new BadRequestException(ErrorCode.INVALID_PAYLOAD_FORMAT, "MQTT 메시지 형식이 잘못되었습니다.");
        } catch (Exception e) {
            log.error("Elevator MQTT 처리 실패");
            //       [본인 기능]
        }
    }

    @Override
    public MqttServiceType getServiceType() {
        return MqttServiceType.ELEVATOR;
    }  // ------ 수정할 부분 ------
    //                                                             [본인 기능]
}


