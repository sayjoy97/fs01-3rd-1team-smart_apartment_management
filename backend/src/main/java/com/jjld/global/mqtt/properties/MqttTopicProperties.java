package com.jjld.global.mqtt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.mqtt.topic")
@Data
public class MqttTopicProperties {
    // .ymal파일에서 등록했던 토픽을 아래와 같이 작성
    // 예시 - private String [본인 기능];
    private String garden;
    private String elevator;



    // 작성 후 topic - MqttTopicRegistry로 이동
}
