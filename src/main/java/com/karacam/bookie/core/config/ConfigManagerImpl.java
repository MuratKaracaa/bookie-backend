package com.karacam.bookie.core.config;

import com.karacam.bookie.entities.AppConfig;
import com.karacam.bookie.repositories.ConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ConfigManagerImpl implements ConfigManager {
    private final ConfigRepository configRepository;
    private Map<String, String> configModelMap;

    @Autowired
    public ConfigManagerImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @PostConstruct
    public void init() {
        List<AppConfig> configList = configRepository.findAll();
        Map<String, String> configModelMapInternal = new HashMap<>();
        for (AppConfig config : configList) {
            configModelMapInternal.put(config.getKey(), config.getValue());
        }
        this.configModelMap = configModelMapInternal;
    }

    @Override
    public String getConfig(String configKey) {
        return configModelMap.get(configKey);
    }

    @Override
    public int getIntConfig(String configKey, int fallback) {
        try {
            String value = configModelMap.get(configKey);
            if (value == null || value.isEmpty()) {
                return fallback;
            }
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    @Override
    public boolean getBoolConfig(String configKey, boolean fallback) {
        try {
            String value = configModelMap.get(configKey);
            if (value == null || value.isEmpty()) {
                return fallback;
            }
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return fallback;
        }
    }


    @Override
    public void resetOneConfig(String configKey) {
        Optional<AppConfig> configOptional = configRepository.findById(configKey);
        if (configOptional.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Config with specified key %s does not exist", configKey));
        }

        AppConfig config = configOptional.get();
        configModelMap.put(configKey, config.getValue());
    }

    @Override
    public void resetAllConfigs() {
        List<AppConfig> configList = configRepository.findAll();
        Map<String, String> configModelMapInternal = new HashMap<>();
        for (AppConfig config : configList) {
            configModelMapInternal.put(config.getKey(), config.getValue());
        }
        this.configModelMap = configModelMapInternal;
    }
}
