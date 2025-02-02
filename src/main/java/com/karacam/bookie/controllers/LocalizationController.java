package com.karacam.bookie.controllers;

import com.karacam.bookie.dtos.request.ResetOneLocalizationRequest;
import com.karacam.bookie.services.LocalizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/localization")
public class LocalizationController {

    private final LocalizationService localizationService;

    @Autowired
    public LocalizationController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @PutMapping
    public void resetOneLocalization(@Valid @RequestBody ResetOneLocalizationRequest body) {
        this.localizationService.resetOneLocalization(body.getKey());
    }

    @PutMapping("/reset")
    public void resetAllLocalizations() {
        this.localizationService.resetAllLocalizations();
    }
}
