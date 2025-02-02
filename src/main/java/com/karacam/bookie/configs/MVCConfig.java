package com.karacam.bookie.configs;

import com.karacam.bookie.core.flow.FlowStepInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MVCConfig implements WebMvcConfigurer {
    private final FlowStepInterceptor flowStepInterceptor;

    @Autowired
    public MVCConfig(FlowStepInterceptor flowStepInterceptor) {
        this.flowStepInterceptor = flowStepInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.flowStepInterceptor);
    }

}
