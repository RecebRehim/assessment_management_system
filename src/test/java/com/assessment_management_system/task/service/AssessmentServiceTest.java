package com.assessment_management_system.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.assessment_management_system.task.dto.AssessmentResponse;
import com.assessment_management_system.task.dto.CreateAssessmentRequest;
import com.assessment_management_system.task.dto.UpdateAssessmentRequest;
import com.assessment_management_system.task.entity.Assessment;
import com.assessment_management_system.task.entity.User;
import com.assessment_management_system.task.enums.AssessmentStatus;
import com.assessment_management_system.task.enums.Role;
import com.assessment_management_system.task.exception.ResourceNotFoundException;
import com.assessment_management_system.task.repository.AssessmentRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AssessmentService assessmentService;

    private User admin;

    @BeforeEach
    void setUp() {
        admin = new User();
        admin.setId(1L);
        admin.setName("Admin");
        admin.setEmail("admin@assessment.local");
        admin.setRole(Role.ADMIN);
    }

    @Test
    void createStoresDraftAssessmentForAuthenticatedUser() {
        when(currentUserService.requireCurrentUser()).thenReturn(admin);
        when(assessmentRepository.save(any(Assessment.class))).thenAnswer(invocation -> {
            Assessment assessment = invocation.getArgument(0);
            assessment.setId(42L);
            return assessment;
        });

        AssessmentResponse response = assessmentService.create(
                new CreateAssessmentRequest("  Quarterly review  ", "  Q1  ")
        );

        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.title()).isEqualTo("Quarterly review");
        assertThat(response.status()).isEqualTo(AssessmentStatus.DRAFT);
        assertThat(response.createdBy().email()).isEqualTo("admin@assessment.local");

        ArgumentCaptor<Assessment> captor = ArgumentCaptor.forClass(Assessment.class);
        verify(assessmentRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedBy()).isEqualTo(admin);
    }

    @Test
    void updateChangesMutableFieldsOnly() {
        Assessment existing = new Assessment();
        existing.setId(7L);
        existing.setTitle("Old");
        existing.setDescription("Old desc");
        existing.setStatus(AssessmentStatus.DRAFT);
        existing.setCreatedBy(admin);

        when(assessmentRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(assessmentRepository.save(any(Assessment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AssessmentResponse response = assessmentService.update(
                7L,
                new UpdateAssessmentRequest("New title", "New desc", AssessmentStatus.ACTIVE)
        );

        assertThat(response.title()).isEqualTo("New title");
        assertThat(response.status()).isEqualTo(AssessmentStatus.ACTIVE);
        assertThat(response.createdBy().id()).isEqualTo(1L);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(assessmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
