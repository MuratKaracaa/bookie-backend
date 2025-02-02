package com.karacam.bookie.services;

import com.karacam.bookie.core.pattern.PatternManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatternService {
    private final PatternManager patternManager;

    @Autowired
    public PatternService(PatternManager patternManager) {
        this.patternManager = patternManager;
    }

    public void resetOnePattern(String key) {
        this.patternManager.resetOnePattern(key);
    }

    public void resetAllPatters() {
        this.patternManager.resetAllPatterns();
    }
}
