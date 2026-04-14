package com.ota.travi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync // sau này dùng cho notification, AI processing
@EnableScheduling //sau này dùng cho dynamic pricing, smart notification
public class TraViOtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraViOtaApplication.class, args);
    }

}

