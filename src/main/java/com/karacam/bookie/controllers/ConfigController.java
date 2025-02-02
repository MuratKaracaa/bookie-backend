package com.karacam.bookie.controllers;

import com.karacam.bookie.dtos.request.ResetOneConfigRequest;
import com.karacam.bookie.services.ConfigService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configs")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @PutMapping
    public void resetOneConfig(@Valid @RequestBody ResetOneConfigRequest requestBody) {
        this.configService.resetOneConfig(requestBody.getKey());
    }

    @PutMapping("/reset")
    public void resetAllConfigs() {
        this.configService.resetAllConfigs();
    }
}
