package com.karacam.bookie.core.constraints.validators;

import com.karacam.bookie.core.SpringApplicationContext;
import com.karacam.bookie.core.constraints.annotations.ValidField;
import com.karacam.bookie.core.enums.PatternCategory;
import com.karacam.bookie.core.localization.LocalizationManager;
import com.karacam.bookie.core.models.PatternModel;
import com.karacam.bookie.core.pattern.PatternManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FieldValidator implements ConstraintValidator<ValidField, String> {

    private PatternModel pattern;

    private PatternCategory patternCategory;
    private boolean nullable;
    private PatternManager patternManager = SpringApplicationContext.getBean(PatternManager.class);

    @Override
    public void initialize(ValidField constraintAnnotation) {
        this.pattern = patternManager.getPatternModel(constraintAnnotation.patternKey());
        this.patternCategory = constraintAnnotation.category();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String fieldValue, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        LocalizationManager localizationManager = SpringApplicationContext.getBean(LocalizationManager.class);
        String error = "Default error message";

        if (pattern == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "VAL-003");
        }

        if (!this.nullable && fieldValue == null) {
            if (patternCategory == PatternCategory.DTO) {
                error = localizationManager.getLocalization("null_field_not_accepted_error");
                context.buildConstraintViolationWithTemplate(error).addPropertyNode(pattern.getErrorCode()).addConstraintViolation();
            } else {
                error = "VAL-001";
                context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            }
            return false;
        }

        if (fieldValue.matches(pattern.getPattern())) {
            return true;
        } else {
            if (patternCategory == PatternCategory.DTO) {
                String localizationKey = pattern.getLocalizationKey();
                if (!localizationKey.isEmpty()) {
                    error = localizationManager.getLocalization(localizationKey);
                }
                context.buildConstraintViolationWithTemplate(error).addPropertyNode(pattern.getErrorCode()).addConstraintViolation();
            } else {
                error = pattern.getErrorCode();
                context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            }
            return false;
        }
    }

}
