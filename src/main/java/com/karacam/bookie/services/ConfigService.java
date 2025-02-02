package com.karacam.bookie.services;

import com.karacam.bookie.core.config.ConfigManager;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    private final ConfigManager configManager;

    public ConfigService(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void resetOneConfig(String configKey) {
        this.configManager.resetOneConfig(configKey);
    }

    public void resetAllConfigs() {
        this.configManager.resetAllConfigs();
    }
}
