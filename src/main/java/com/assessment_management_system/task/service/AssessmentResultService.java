package com.assessment_management_system.task.service;

import com.assessment_management_system.task.dto.AssessmentResultResponse;
import com.assessment_management_system.task.dto.CreateResultRequest;
import com.assessment_management_system.task.entity.Assessment;
import com.assessment_management_system.task.entity.AssessmentResult;
import com.assessment_management_system.task.enums.AssessmentStatus;
import com.assessment_management_system.task.exception.DuplicateResourceException;
import com.assessment_management_system.task.exception.ResourceNotFoundException;
import com.assessment_management_system.task.repository.AssessmentResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssessmentResultService {

    private final AssessmentService assessmentService;
    private final AssessmentResultRepository resultRepository;

    public AssessmentResultService(
            AssessmentService assessmentService,
            AssessmentResultRepository resultRepository
    ) {
        this.assessmentService = assessmentService;
        this.resultRepository = resultRepository;
    }

    @Transactional
    public AssessmentResultResponse create(Long assessmentId, CreateResultRequest request) {
        Assessment assessment = assessmentService.getById(assessmentId);
        if (resultRepository.existsByAssessmentId(assessmentId)) {
            throw new DuplicateResourceException("Result already exists for assessment: " + assessmentId);
        }

        AssessmentResult result = new AssessmentResult();
        result.setAssessment(assessment);
        result.setScore(request.score());
        result.setResultStatus(ScoreEvaluator.fromScore(request.score()));
        AssessmentResult saved = resultRepository.save(result);

        assessment.setStatus(AssessmentStatus.COMPLETED);
        assessment.setResult(saved);
        return AssessmentResultResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public AssessmentResultResponse findByAssessmentId(Long assessmentId) {
        assessmentService.getById(assessmentId);
        return AssessmentResultResponse.from(getByAssessmentId(assessmentId));
    }

    @Transactional(readOnly = true)
    public AssessmentResult getByAssessmentId(Long assessmentId) {
        return resultRepository.findByAssessmentId(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Result not found for assessment: " + assessmentId
                ));
    }
}