package com.jjld.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChangeObjectConfig {
    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);  // STRICT 모드에서는 이름 거의 정확히 같을 때만 매핑함
        return mapper;
    }

//    @Bean
//    public ObjectMapper getObjectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper;
//    }
}
