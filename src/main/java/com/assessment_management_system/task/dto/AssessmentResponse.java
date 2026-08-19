package com.assessment_management_system.task.dto;

import com.assessment_management_system.task.entity.Assessment;
import com.assessment_management_system.task.enums.AssessmentStatus;
import java.time.Instant;

public record AssessmentResponse(
        Long id,
        String title,
        String description,
        AssessmentStatus status,
        Instant createdAt,
        UserResponse createdBy
) {
    public static AssessmentResponse from(Assessment assessment) {
        return new AssessmentResponse(
                assessment.getId(),
                assessment.getTitle(),
                assessment.getDescription(),
                assessment.getStatus(),
                assessment.getCreatedAt(),
                UserResponse.from(assessment.getCreatedBy())
        );
    }
}