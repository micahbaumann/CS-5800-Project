package com.chachef.controller;

import com.chachef.components.AuthRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthRequiredInterceptor authRequiredInterceptor;

    @Autowired
    public WebConfig(AuthRequiredInterceptor authRequiredInterceptor) {
        this.authRequiredInterceptor = authRequiredInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authRequiredInterceptor)
                .addPathPatterns("/**"); // or narrow to /api/** etc.
    }
}
