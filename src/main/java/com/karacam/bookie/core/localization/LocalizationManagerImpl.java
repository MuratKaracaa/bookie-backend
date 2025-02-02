package com.karacam.bookie.core.localization;

import com.karacam.bookie.core.models.LocalizationModel;
import com.karacam.bookie.entities.Localization;
import com.karacam.bookie.repositories.LocalizationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Component
public class LocalizationManagerImpl implements LocalizationManager {

    private final LocalizationRepository localizationRepository;
    private Map<String, LocalizationModel> localizationMap;

    @Autowired
    public LocalizationManagerImpl(LocalizationRepository localizationRepository) {
        this.localizationRepository = localizationRepository;
    }

    @PostConstruct
    public void init() {
        List<Localization> localizationList = localizationRepository.findAll();
        Map<String, LocalizationModel> localizationMapInternal = new HashMap<>();
        for (Localization localization : localizationList) {
            LocalizationModel localizationModel = LocalizationModel.builder()
                    .tr(localization.getTrTr())
                    .en(localization.getEnUs())
                    .build();
            localizationMapInternal.put(localization.getKey(), localizationModel);
        }
        this.localizationMap = localizationMapInternal;
    }

    @Override
    public String getLocalization(String localizationKey) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Locale locale = request.getLocale();
        LocalizationModel model = localizationMap.get(localizationKey);
        return locale == Locale.ENGLISH ? model.getEn() : model.getTr();
    }

    @Override
    public void resetOneLocalization(String localizationKey) {
        Optional<Localization> localizationOptional = localizationRepository.findById(localizationKey);
        if (localizationOptional.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Localization with specified key %s does not exist", localizationKey));
        }

        Localization localization = localizationOptional.get();

        LocalizationModel localizationModel = LocalizationModel.builder()
                .tr(localization.getTrTr())
                .en(localization.getEnUs())
                .build();

        this.localizationMap.put(localizationKey, localizationModel);
    }

    @Override
    public void resetAllLocalizations() {
        List<Localization> localizations = localizationRepository.findAll();
        Map<String, LocalizationModel> localizationMap = new HashMap<>();
        for (Localization localization : localizations) {
            LocalizationModel localizationModel = LocalizationModel.builder()
                    .tr(localization.getTrTr())
                    .en(localization.getEnUs())
                    .build();

            localizationMap.put(localization.getKey(), localizationModel);
        }
        this.localizationMap.clear();
        this.localizationMap.putAll(localizationMap);
    }


}
