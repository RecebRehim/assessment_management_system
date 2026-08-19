package com.assessment_management_system.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ollama")
public record OllamaProperties(
        String baseUrl,
        String model,
        int timeoutSeconds
) {
}