package com.silverwing.dorothy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DorothyApplication {
    public static final String COOKIE_NAME="DORCOOKIE";
    public static void main(String[] args) {
        SpringApplication.run(DorothyApplication.class, args);
    }
}
