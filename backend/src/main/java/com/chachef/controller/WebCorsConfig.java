package com.chachef.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")   // allow ALL origins
                .allowedMethods("*")          // allow ALL HTTP methods
                .allowedHeaders("*")          // allow ALL headers
                .allowCredentials(true);      // allow cookies/auth headers if needed
    }
}
