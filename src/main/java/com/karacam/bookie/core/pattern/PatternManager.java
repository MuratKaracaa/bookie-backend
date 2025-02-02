package com.karacam.bookie.core.pattern;

import com.karacam.bookie.core.models.PatternModel;
import org.springframework.stereotype.Component;

@Component
public interface PatternManager {
    PatternModel getPatternModel(String patternKey);

    void resetOnePattern(String patternKey);

    void resetAllPatterns();
}
