package com.assessment_management_system.task.controller;

import com.assessment_management_system.task.dto.AiSummaryResponse;
import com.assessment_management_system.task.dto.AssessmentResponse;
import com.assessment_management_system.task.dto.AssessmentResultResponse;
import com.assessment_management_system.task.dto.CreateAssessmentRequest;
import com.assessment_management_system.task.dto.CreateResultRequest;
import com.assessment_management_system.task.dto.UpdateAssessmentRequest;
import com.assessment_management_system.task.service.AiSummaryService;
import com.assessment_management_system.task.service.AssessmentResultService;
import com.assessment_management_system.task.service.AssessmentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final AssessmentResultService resultService;
    private final AiSummaryService aiSummaryService;

    public AssessmentController(
            AssessmentService assessmentService,
            AssessmentResultService resultService,
            AiSummaryService aiSummaryService
    ) {
        this.assessmentService = assessmentService;
        this.resultService = resultService;
        this.aiSummaryService = aiSummaryService;
    }

    @PostMapping
    public ResponseEntity<AssessmentResponse> create(@Valid @RequestBody CreateAssessmentRequest request) {
        AssessmentResponse created = assessmentService.create(request);
        return ResponseEntity.created(URI.create("/assessments/" + created.id())).body(created);
    }

    @GetMapping
    public List<AssessmentResponse> findAll() {
        return assessmentService.findAll();
    }

    @GetMapping("/{id}")
    public AssessmentResponse findById(@PathVariable Long id) {
        return assessmentService.findById(id);
    }

    @PutMapping("/{id}")
    public AssessmentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssessmentRequest request
    ) {
        return assessmentService.update(id, request);
    }

    @PostMapping("/{id}/result")
    public ResponseEntity<AssessmentResultResponse> createResult(
            @PathVariable Long id,
            @Valid @RequestBody CreateResultRequest request
    ) {
        AssessmentResultResponse created = resultService.create(id, request);
        return ResponseEntity.created(URI.create("/assessments/" + id + "/result")).body(created);
    }

    @GetMapping("/{id}/result")
    public AssessmentResultResponse getResult(@PathVariable Long id) {
        return resultService.findByAssessmentId(id);
    }

    @PostMapping("/{id}/ai-summary")
    public AiSummaryResponse generateAiSummary(@PathVariable Long id) {
        return aiSummaryService.generateSummary(id);
    }
}