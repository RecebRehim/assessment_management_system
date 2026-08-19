package com.assessment_management_system.task.service;

import com.assessment_management_system.task.dto.AssessmentResponse;
import com.assessment_management_system.task.dto.CreateAssessmentRequest;
import com.assessment_management_system.task.dto.UpdateAssessmentRequest;
import com.assessment_management_system.task.entity.Assessment;
import com.assessment_management_system.task.entity.User;
import com.assessment_management_system.task.enums.AssessmentStatus;
import com.assessment_management_system.task.exception.ResourceNotFoundException;
import com.assessment_management_system.task.repository.AssessmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final CurrentUserService currentUserService;

    public AssessmentService(AssessmentRepository assessmentRepository, CurrentUserService currentUserService) {
        this.assessmentRepository = assessmentRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public AssessmentResponse create(CreateAssessmentRequest request) {
        User creator = currentUserService.requireCurrentUser();
        Assessment assessment = new Assessment();
        assessment.setTitle(request.title().trim());
        assessment.setDescription(normalizeDescription(request.description()));
        assessment.setStatus(AssessmentStatus.DRAFT);
        assessment.setCreatedBy(creator);
        return AssessmentResponse.from(assessmentRepository.save(assessment));
    }

    @Transactional(readOnly = true)
    public List<AssessmentResponse> findAll() {
        return assessmentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(AssessmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssessmentResponse findById(Long id) {
        return AssessmentResponse.from(getById(id));
    }

    @Transactional
    public AssessmentResponse update(Long id, UpdateAssessmentRequest request) {
        Assessment assessment = getById(id);
        assessment.setTitle(request.title().trim());
        assessment.setDescription(normalizeDescription(request.description()));
        assessment.setStatus(request.status());
        return AssessmentResponse.from(assessmentRepository.save(assessment));
    }

    @Transactional(readOnly = true)
    public Assessment getById(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + id));
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}