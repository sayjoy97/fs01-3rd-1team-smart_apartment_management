package com.jjld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@EntityScan(basePackages = "com.jjld.domain")
@IntegrationComponentScan
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
