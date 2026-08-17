package com.assessment_management_system.task.repository;

import com.assessment_management_system.task.entity.AssessmentResult;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    boolean existsByAssessmentId(Long assessmentId);

    @EntityGraph(attributePaths = "assessment")
    Optional<AssessmentResult> findByAssessmentId(Long assessmentId);
}

