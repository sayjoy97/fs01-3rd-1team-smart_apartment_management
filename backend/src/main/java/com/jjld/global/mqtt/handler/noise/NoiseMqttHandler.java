package com.jjld.global.mqtt.handler.noise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjld.domain.noise.dto.NoiseEventRequest;
import com.jjld.domain.noise.service.NoiseFlowService;
import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.mqtt.handler.MqttMessageHandler;
import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//          이름 수정
@Component("noiseMqttHandler")  // 어노테이션들 복붙
@RequiredArgsConstructor
@Slf4j
public class NoiseMqttHandler implements MqttMessageHandler {
    private final NoiseFlowService noiseFlowService;  // ------ 수정할 부분 ------
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicInfo topicInfo, String payload) {
        // ✅ 기대 토픽: jjld/noise/{sensorId}/{serviceType}
        Long sensorId = Long.parseLong(topicInfo.getFullTopicParts()[2]);
        NoiseServiceType serviceType = NoiseServiceType.from(topicInfo.getFullTopicParts()[3]);
        // 토픽을 파싱해서 사용
        // garden의 경우 jjld/garden/[gardenId]/serviceType 으로 설정함
        // topicInfo.getFullTopicParts()[i] i를 설정해서 원하는 값을 파싱

        try {
            switch (serviceType) {
                case EVENT -> {
                    NoiseEventRequest request =
                            objectMapper.readValue(payload, NoiseEventRequest.class);
                    if (request.getSoundLevel() == null) {
                        throw new BadRequestException(ErrorCode.INVALID_PAYLOAD_FORMAT, "soundLevel이 없습니다.");
                    }
                    noiseFlowService.receiveNoiseEvent(sensorId, request.getSoundLevel());
                    log.info("NOISE MQTT EVENT 처리 완료: sensorId={}, soundLevel={}", sensorId, request.getSoundLevel());
                }
            }
        } catch (BusinessException e) {
            throw new BadRequestException(ErrorCode.INVALID_PAYLOAD_FORMAT, "MQTT 메시지 형식이 잘못되었습니다.");
        } catch (Exception e) {
            log.error("Noise MQTT 처리 실패");
        }
    }

    @Override
    public MqttServiceType getServiceType() {
        return MqttServiceType.NOISE;
    }  // ------ 수정할 부분 ------
    //                                                             [본인 기능]
}
