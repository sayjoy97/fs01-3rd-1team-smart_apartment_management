package com.jjld.global.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChangeObjectConfig {
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}
