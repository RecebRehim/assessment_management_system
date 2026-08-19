package com.assessment_management_system.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.assessment_management_system.task.dto.AssessmentResultResponse;
import com.assessment_management_system.task.dto.CreateResultRequest;
import com.assessment_management_system.task.entity.Assessment;
import com.assessment_management_system.task.entity.AssessmentResult;
import com.assessment_management_system.task.entity.User;
import com.assessment_management_system.task.enums.AssessmentStatus;
import com.assessment_management_system.task.enums.ResultStatus;
import com.assessment_management_system.task.enums.Role;
import com.assessment_management_system.task.exception.DuplicateResourceException;
import com.assessment_management_system.task.repository.AssessmentResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentResultServiceTest {

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessmentResultRepository resultRepository;

    @InjectMocks
    private AssessmentResultService resultService;

    private Assessment assessment;

    @BeforeEach
    void setUp() {
        User creator = new User();
        creator.setId(1L);
        creator.setName("Admin");
        creator.setEmail("admin@assessment.local");
        creator.setRole(Role.ADMIN);

        assessment = new Assessment();
        assessment.setId(3L);
        assessment.setTitle("Review");
        assessment.setStatus(AssessmentStatus.ACTIVE);
        assessment.setCreatedBy(creator);
    }

    @Test
    void createCalculatesResultStatusAndMarksAssessmentCompleted() {
        when(assessmentService.getById(3L)).thenReturn(assessment);
        when(resultRepository.existsByAssessmentId(3L)).thenReturn(false);
        when(resultRepository.save(any(AssessmentResult.class))).thenAnswer(invocation -> {
            AssessmentResult result = invocation.getArgument(0);
            result.setId(11L);
            return result;
        });

        AssessmentResultResponse response = resultService.create(3L, new CreateResultRequest(88));

        assertThat(response.score()).isEqualTo(88);
        assertThat(response.resultStatus()).isEqualTo(ResultStatus.EXCELLENT);
        assertThat(assessment.getStatus()).isEqualTo(AssessmentStatus.COMPLETED);
    }

    @Test
    void createRejectsSecondResultForSameAssessment() {
        when(assessmentService.getById(3L)).thenReturn(assessment);
        when(resultRepository.existsByAssessmentId(3L)).thenReturn(true);

        assertThatThrownBy(() -> resultService.create(3L, new CreateResultRequest(70)))
                .isInstanceOf(DuplicateResourceException.class);

        verify(resultRepository, never()).save(any());
    }
}