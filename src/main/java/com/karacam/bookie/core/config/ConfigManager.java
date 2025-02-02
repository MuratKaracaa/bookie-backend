package com.karacam.bookie.core.config;

import org.springframework.stereotype.Component;

@Component
public interface ConfigManager {
    String getConfig(String configKey);

    int getIntConfig(String configKey, int fallback);

    boolean getBoolConfig(String configKey, boolean fallback);

    void resetOneConfig(String configKey);

    void resetAllConfigs();
}
