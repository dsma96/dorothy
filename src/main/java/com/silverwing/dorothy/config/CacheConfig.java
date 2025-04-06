package com.silverwing.dorothy.config;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Caffeine;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(100));
        cacheManager.setCacheNames(List.of("reservation","member","offday","hairservice","availableDesigners"));
        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }
}
