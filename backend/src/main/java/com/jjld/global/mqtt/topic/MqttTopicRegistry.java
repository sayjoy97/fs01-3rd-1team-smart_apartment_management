package com.jjld.global.mqtt.topic;

import com.jjld.global.mqtt.properties.MqttTopicProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MqttTopicRegistry {

    private final MqttTopicProperties properties;

    private Map<MqttServiceType, String> topicMap;

    @PostConstruct
    public void init() {
        topicMap = Map.of(
                // 예시 - MqttServiceType.[본인 기능], properties.get[본인 기능](),
                MqttServiceType.GARDEN, properties.getGarden(),
                MqttServiceType.ELEVATOR, properties.getElevator(),
                MqttServiceType.ENERGY, properties.getEnergy()
                MqttServiceType.CARGATE, properties.getCargate()


                // 작성 후 handler 패키지에 본인 기능 패키지를 만듦
                // 예시
                // handler
                //  ├── MqttMessageHandler
                //  ├── garden
                //  │    ├── GardenMqttHandler
                //  │    └── GardenServiceType
                //  ├── [본인 기능]
                //  │    ├── [본인 기능]MqttHandler
                //  │    └── [본인 기능]ServiceType
                //
                // GardenMqttHandler와 GardenServiceType로 이동하여 확인
                // enum으로 switch문을 만들지 않을 경우 GardenServiceType파일을 만들지 않아도 됨
        );
    }

    public String getTopic(MqttServiceType type) {
        return topicMap.get(type);
    }

    public Collection<String> getAllTopics() {
        return topicMap.values();
    }
}
