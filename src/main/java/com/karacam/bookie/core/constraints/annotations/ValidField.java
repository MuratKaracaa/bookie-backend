package com.karacam.bookie.core.constraints.annotations;

import com.karacam.bookie.core.constraints.validators.FieldValidator;
import com.karacam.bookie.core.enums.PatternCategory;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldValidator.class)
public @interface ValidField {
    String message() default "Default Invalid Parameter Message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    PatternCategory category() default PatternCategory.DTO;

    String patternKey() default "";

    boolean nullable() default false;
}
