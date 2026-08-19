package com.assessment_management_system.task.bootstrap;

import com.assessment_management_system.task.config.BootstrapProperties;
import com.assessment_management_system.task.entity.User;
import com.assessment_management_system.task.enums.Role;
import com.assessment_management_system.task.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DefaultUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BootstrapProperties properties;

    public DefaultUserInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            BootstrapProperties properties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        createIfMissing(properties.adminName(), properties.adminEmail(), properties.adminPassword(), Role.ADMIN);
        createIfMissing(properties.analystName(), properties.analystEmail(), properties.analystPassword(), Role.ANALYST);
    }

    private void createIfMissing(String name, String email, String rawPassword, Role role) {
        if (name == null || email == null || rawPassword == null || email.isBlank()) {
            log.warn("Skipping default {} user because bootstrap properties are incomplete", role);
            return;
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email.toLowerCase());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        userRepository.save(user);
        log.info("Created default {} user: {}", role, email);
    }
}