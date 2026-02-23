package com.jjld.global.mqtt.handler.energy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjld.domain.energy.dto.EnergyMeasurementCreateRequest;
import com.jjld.domain.energy.service.EnergyMeasurementService;
import com.jjld.domain.garden.service.GardenService;
import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.mqtt.handler.MqttMessageHandler;
import com.jjld.global.mqtt.handler.garden.GardenServiceType;
import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//          이름 수정
@Component("energyMqttHandler")  // 어노테이션들 복붙
@RequiredArgsConstructor
@Slf4j
public class EnergyMqttHandler implements MqttMessageHandler {
    private final EnergyMeasurementService energyMeasurementService;  // ------ 수정할 부분 ------
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicInfo topicInfo, String payload) {
        // jjld/energy/{deviceId}/{serviceType}
        Long deviceId = Long.parseLong(topicInfo.getFullTopicParts()[2]);
        EnergyServiceType serviceType = EnergyServiceType.from(topicInfo.getFullTopicParts()[3]);
        // 토픽을 파싱해서 사용
        // garden의 경우 jjld/garden/[gardenId]/serviceType 으로 설정함
        // topicInfo.getFullTopicParts()[i] i를 설정해서 원하는 값을 파싱

        try {
            switch (serviceType) {
                case MEASUREMENT -> {
                    EnergyMeasurementCreateRequest request =
                            objectMapper.readValue(payload, EnergyMeasurementCreateRequest.class);

                    request.setDeviceId(deviceId); // 토픽에서 추출한 deviceId 주입
                    energyMeasurementService.createMeasurement(request);
                    log.info("ENERGY MQTT MEASUREMENT 처리 완료: deviceId={}", deviceId);
                }
                case CONTROL_ACK -> {
                    log.info("ENERGY CONTROL ACK 수신: {}", payload);
                }
            }

        } catch (BusinessException e) {
            throw new BadRequestException(ErrorCode.INVALID_PAYLOAD_FORMAT, "MQTT 메시지 형식이 잘못되었습니다.");
        } catch (Exception e) {
            log.error("Energy MQTT 처리 실패");
        }
    }

    @Override
    public MqttServiceType getServiceType() {
        return MqttServiceType.ENERGY;
    }  // ------ 수정할 부분 ------
    //                                                             [본인 기능]
}
