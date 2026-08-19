package com.assessment_management_system.task.controller;

import com.assessment_management_system.task.dto.AssessmentResponse;
import com.assessment_management_system.task.dto.CreateAssessmentRequest;
import com.assessment_management_system.task.service.AssessmentService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
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
}