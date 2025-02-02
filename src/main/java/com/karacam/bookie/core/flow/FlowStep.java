package com.karacam.bookie.core.flow;

import com.karacam.bookie.core.enums.FlowStage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FlowStep {
    String key();

    int step() default 0;

    boolean anonymousFlow() default false;

    FlowStage stage();
}
