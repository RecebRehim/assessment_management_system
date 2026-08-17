package com.assessment_management_system.task.dto;

import com.assessment_management_system.task.enums.AssessmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAssessmentRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @Size(max = 2000, message = "description must be at most 2000 characters")
        String description,

        @NotNull(message = "status must not be null")
        AssessmentStatus status
) {
}