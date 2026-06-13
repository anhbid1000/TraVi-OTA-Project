package com.ota.travi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync // sau nay dung cho notification, AI processing
public class TraViOtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraViOtaApplication.class, args);
    }
}
