package com.karacam.bookie.services;

import com.karacam.bookie.core.localization.LocalizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalizationService {

    private final LocalizationManager localizationManager;

    @Autowired
    public LocalizationService(LocalizationManager localizationManager) {
        this.localizationManager = localizationManager;
    }

    public void resetOneLocalization(String key) {
        this.localizationManager.resetOneLocalization(key);
    }

    public void resetAllLocalizations() {
        this.localizationManager.resetAllLocalizations();
    }
}
