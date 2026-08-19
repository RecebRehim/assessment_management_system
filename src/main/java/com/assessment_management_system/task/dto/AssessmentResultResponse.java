package com.assessment_management_system.task.dto;

import com.assessment_management_system.task.entity.AssessmentResult;
import com.assessment_management_system.task.enums.ResultStatus;
import java.time.Instant;

public record AssessmentResultResponse(
        Long id,
        Long assessmentId,
        Integer score,
        ResultStatus resultStatus,
        Instant createdAt,
        String aiSummary
) {
    public static AssessmentResultResponse from(AssessmentResult result) {
        return new AssessmentResultResponse(
                result.getId(),
                result.getAssessment().getId(),
                result.getScore(),
                result.getResultStatus(),
                result.getCreatedAt(),
                result.getAiSummary()
        );
    }
}