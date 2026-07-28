package com.example.dorothy.service;

import com.example.dorothy.entity.Config;
import com.example.dorothy.repository.ConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public List<Config> getAllConfigurations() {
        return configRepository.findAll();
    }
}
