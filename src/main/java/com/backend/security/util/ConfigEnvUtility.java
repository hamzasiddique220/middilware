package com.backend.security.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
public class ConfigEnvUtility {
    private final Environment env;

    public String getProperty(String pPropertyKey) {
        return env.getProperty(pPropertyKey);
    }
}
