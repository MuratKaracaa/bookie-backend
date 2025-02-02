package com.karacam.bookie.core.pattern;

import com.karacam.bookie.core.models.PatternModel;
import com.karacam.bookie.entities.Pattern;
import com.karacam.bookie.repositories.PatternRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PatternManagerImpl implements PatternManager {
    private final PatternRepository patternRepository;
    private Map<String, PatternModel> patternMap;


    @Autowired
    public PatternManagerImpl(PatternRepository patternRepository) {
        this.patternRepository = patternRepository;
    }

    @PostConstruct
    public void init() {
        List<Pattern> patternList = this.patternRepository.findAll();
        Map<String, PatternModel> patternMapInternal = new HashMap<>();
        for (Pattern pattern : patternList) {
            PatternModel patternModel = PatternModel.builder()
                    .pattern(pattern.getPattern())
                    .localizationKey(pattern.getLocalizationKey())
                    .errorCode(pattern.getErrorCode())
                    .build();

            patternMapInternal.put(pattern.getKey(), patternModel);
        }
        this.patternMap = patternMapInternal;
    }

    @Override
    public PatternModel getPatternModel(String patternKey) {
        return this.patternMap.get(patternKey);
    }

    @Override
    public void resetOnePattern(String patternKey) {
        Optional<Pattern> patternOptional = patternRepository.findById(patternKey);
        if (patternOptional.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Pattern with specified key ½s does not exist", patternKey));
        }

        Pattern pattern = patternOptional.get();

        PatternModel patternModel = PatternModel.builder()
                .errorCode(pattern.getErrorCode())
                .pattern(pattern.getPattern())
                .localizationKey(pattern.getLocalizationKey())
                .build();

        this.patternMap.put(patternKey, patternModel);
    }

    @Override
    public void resetAllPatterns() {
        List<Pattern> patterns = patternRepository.findAll();
        Map<String, PatternModel> patternMapInternal = new HashMap<>();
        for (Pattern pattern : patterns) {
            PatternModel patternModel = PatternModel.builder()
                    .pattern(pattern.getPattern())
                    .localizationKey(pattern.getLocalizationKey())
                    .errorCode(pattern.getErrorCode())
                    .build();
            patternMapInternal.put(pattern.getKey(), patternModel);
        }
        this.patternMap.clear();
        this.patternMap.putAll(patternMapInternal);
    }

}
