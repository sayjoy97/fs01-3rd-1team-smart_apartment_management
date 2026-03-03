package com.jjld.global.mqtt.handler.cargate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjld.domain.cargate.service.CargateService;
import com.jjld.global.exception.BusinessException;
import com.jjld.global.exception.ErrorCode;
import com.jjld.global.exception.businessexceptions.BadRequestException;
import com.jjld.global.mqtt.handler.MqttMessageHandler;
import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//          이름 수정
@Component("cargateMqttHandler")  // 어노테이션들 복붙
@RequiredArgsConstructor
@Slf4j
public class CargateMqttHandler implements MqttMessageHandler {

    // 이 밑으로 복사 후 본인 파일에 붙여넣기

    private final CargateService cargateService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicInfo topicInfo, String payload) {
        CargateServiceType serviceType = CargateServiceType.fromThird(topicInfo.getFullTopicParts()[2]);
        String[] message = payload.split("_");

        try{
            switch (serviceType){
                case ENTRY, EXIT:
                    if (message[4].endsWith(".jpg")) {
                        cargateService.AddToTheAccessLog(payload, serviceType);
                        break;
                    }
                case PAYMENT:
                    cargateService.FeeSettlement(payload, serviceType);
                default:
                    log.info("잘못된 방식");
                    break;
            }

        }catch (BusinessException e){
            throw new BadRequestException(ErrorCode.INVALID_PAYLOAD_FORMAT, "MQTT 메시지 형식이 잘못되었습니다.");
        } catch (Exception e) {
            log.error("Cargate MQTT 처리 실패");
        }
    }

    @Override
    public MqttServiceType getServiceType() {
        return MqttServiceType.CARGATE;
    }
}
