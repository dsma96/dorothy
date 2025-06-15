package com.silverwing.dorothy.domain.service;

import com.silverwing.dorothy.domain.dao.ServiceConfigRepository;
import com.silverwing.dorothy.domain.entity.ServiceConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {
    private final ServiceConfigRepository configRepository;

    public ConfigService(ServiceConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public ServiceConfig getAllConfiguration() {
        return configRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No configuration found"));
    }
}
