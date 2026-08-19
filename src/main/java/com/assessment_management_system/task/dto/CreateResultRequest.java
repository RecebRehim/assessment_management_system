package com.assessment_management_system.task.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateResultRequest(
        @NotNull(message = "score must not be null")
        @Min(value = 0, message = "score must be at least 0")
        @Max(value = 100, message = "score must be at most 100")
        Integer score
) {
}