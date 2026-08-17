package com.assessment_management_system.task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap")
public record BootstrapProperties(
        String adminEmail,
        String adminPassword,
        String adminName,
        String analystEmail,
        String analystPassword,
        String analystName
) {
}
