package com.karacam.bookie.controllers;

import com.karacam.bookie.dtos.request.ResetOnePatternRequest;
import com.karacam.bookie.services.PatternService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pattern")
public class PatternController {

    private final PatternService patternService;

    @Autowired
    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @PutMapping
    public void resetOnePattern(@Valid @RequestBody ResetOnePatternRequest body) {
        this.patternService.resetOnePattern(body.getKey());
    }

    @PutMapping("/reset")
    public void resetAllPatterns() {
        this.patternService.resetAllPatters();
    }

}
