package com.jjld.global.mqtt.handler.entrance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjld.domain.entrancedoor.service.EntranceMqttService;
import com.jjld.global.exception.BusinessException;
import com.jjld.global.mqtt.handler.MqttMessageHandler;
import com.jjld.global.mqtt.topic.MqttServiceType;
import com.jjld.global.mqtt.topic.TopicInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//          이름 수정
@Component("entranceMqttHandler")  // 어노테이션들 복붙
@RequiredArgsConstructor
@Slf4j
public class EntranceMqttHandler implements MqttMessageHandler {

    // 이 밑으로 복사 후 본인 파일에 붙여넣기

    private final EntranceMqttService entranceMqttService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(TopicInfo topicInfo, String payload) {

        // 토픽을 파싱해서 사용
        // garden의 경우 jjld/garden/[gardenId]/serviceType 으로 설정함
        // topicInfo.getFullTopicParts()[i] i를 설정해서 원하는 값을 파싱
        String[] parts = topicInfo.getFullTopicParts();

        if (parts.length > 2 && "door".equals(parts[2])) {
            return;
        }
        try {
            Integer houseDong = Integer.parseInt(topicInfo.getFullTopicParts()[2]);
            String serviceTypeStr = topicInfo.getFullTopicParts()[3];

            EntranceServiceType serviceType = EntranceServiceType.from(serviceTypeStr);

            String cardUid = payload.trim();

            switch (serviceType) {  // 이곳에서 해당하는 gardenService의 메서드를 호출
                case CARD -> {
                    entranceMqttService.handleCard(houseDong, cardUid);
                }
                case PASS -> {
                    String[] passParts = payload.trim().split("#");

                    if(passParts.length != 2){
                        log.warn("잘못된 PASS payload 형식: {}", payload);
                        return;
                    }

                    Integer houseHo = Integer.parseInt(passParts[0]);
                    String rawPass = passParts[1];

                    entranceMqttService.handlePass(houseDong, houseHo, rawPass);
                }

                default -> log.warn("지원하지 않는 출입 서비스 타입: {}", serviceType);
            }

        } catch (BusinessException e) {
            log.error("비즈니스 로직 에러 발생! 진짜 원인: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Entrance MQTT 처리 실패", e);
        }
    }

    @Override
    public MqttServiceType getServiceType() {
        return MqttServiceType.ENTRANCE;
    }  // ------ 수정할 부분 ------
    //
}
