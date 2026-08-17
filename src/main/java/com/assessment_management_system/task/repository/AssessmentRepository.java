package com.assessment_management_system.task.repository;

import com.assessment_management_system.task.entity.Assessment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    @EntityGraph(attributePaths = "createdBy")
    List<Assessment> findAllByOrderByCreatedAtDesc();

    @Override
    @EntityGraph(attributePaths = "createdBy")
    Optional<Assessment> findById(Long id);
}
