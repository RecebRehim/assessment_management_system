package com.assessment_management_system.task.service;

import com.assessment_management_system.task.client.OllamaClient;
import com.assessment_management_system.task.dto.AiSummaryResponse;
import com.assessment_management_system.task.entity.Assessment;
import com.assessment_management_system.task.entity.AssessmentResult;
import com.assessment_management_system.task.repository.AssessmentResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiSummaryService {

    private final AssessmentResultService resultService;
    private final AssessmentResultRepository resultRepository;
    private final OllamaClient ollamaClient;

    public AiSummaryService(
            AssessmentResultService resultService,
            AssessmentResultRepository resultRepository,
            OllamaClient ollamaClient
    ) {
        this.resultService = resultService;
        this.resultRepository = resultRepository;
        this.ollamaClient = ollamaClient;
    }

    @Transactional
    public AiSummaryResponse generateSummary(Long assessmentId) {
        AssessmentResult result = resultService.getByAssessmentId(assessmentId);
        Assessment assessment = result.getAssessment();

        String prompt = """
                You are a reporting assistant. Write a short professional summary (3-5 sentences) of an assessment result.
                You must not change, recompute, or dispute the score or result status. Treat them as final facts decided by the system.

                Title: %s
                Description: %s
                Score: %d/100 (already calculated, immutable)
                Result status: %s (already decided, immutable)

                Write only the summary text.
                """.formatted(
                assessment.getTitle(),
                assessment.getDescription() == null ? "" : assessment.getDescription(),
                result.getScore(),
                result.getResultStatus().name()
        );

        String summary = ollamaClient.generate(prompt);
        result.setAiSummary(summary);
        resultRepository.save(result);

        return new AiSummaryResponse(
                assessmentId,
                result.getScore(),
                result.getResultStatus().name(),
                summary
        );
    }
}