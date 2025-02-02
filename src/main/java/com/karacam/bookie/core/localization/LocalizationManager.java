package com.karacam.bookie.core.localization;

import org.springframework.stereotype.Component;

@Component
public interface LocalizationManager {
    String getLocalization(String localizationKey);

    void resetOneLocalization(String localizationKey);

    void resetAllLocalizations();
}
