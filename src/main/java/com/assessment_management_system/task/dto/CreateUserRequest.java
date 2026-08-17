package com.assessment_management_system.task.dto;

import com.assessment_management_system.task.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "name must not be blank")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @NotBlank(message = "email must not be blank")
        @Email(message = "email must be a valid email address")
        String email,

        @NotBlank(message = "password must not be blank")
        @Size(min = 8, max = 72, message = "password must be between 8 and 72 characters")
        String password,

        @NotNull(message = "role must not be null")
        Role role
) {
}
