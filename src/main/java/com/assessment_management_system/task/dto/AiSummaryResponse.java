package com.assessment_management_system.task.dto;

public record AiSummaryResponse(
        Long assessmentId,
        Integer score,
        String resultStatus,
        String summary
) {
}