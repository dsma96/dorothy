package com.silverwing.dorothy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class DorothyApplication {
    public static final String COOKIE_NAME="DORCOOKIE";
    public static void main(String[] args) {
        SpringApplication.run(DorothyApplication.class, args);
    }
}
