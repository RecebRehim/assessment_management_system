package com.assessment_management_system.task.dto;

import com.assessment_management_system.task.entity.User;
import com.assessment_management_system.task.enums.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}

