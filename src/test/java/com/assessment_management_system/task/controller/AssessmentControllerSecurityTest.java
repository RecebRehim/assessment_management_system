package com.assessment_management_system.task.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assessment_management_system.task.config.SecurityConfig;
import com.assessment_management_system.task.dto.AssessmentResponse;
import com.assessment_management_system.task.dto.UserResponse;
import com.assessment_management_system.task.enums.AssessmentStatus;
import com.assessment_management_system.task.enums.Role;
import com.assessment_management_system.task.service.AiSummaryService;
import com.assessment_management_system.task.service.AssessmentResultService;
import com.assessment_management_system.task.service.AssessmentService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AssessmentController.class)
@Import(SecurityConfig.class)
class AssessmentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssessmentService assessmentService;

    @MockitoBean
    private AssessmentResultService resultService;

    @MockitoBean
    private AiSummaryService aiSummaryService;

    @Test
    @WithMockUser(roles = "ANALYST")
    void analystCannotCreateAssessment() throws Exception {
        mockMvc.perform(post("/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Review\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateAssessment() throws Exception {
        when(assessmentService.create(any())).thenReturn(sampleAssessment());

        mockMvc.perform(post("/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Review\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ANALYST")
    void analystCanReadAssessments() throws Exception {
        when(assessmentService.findAll()).thenReturn(List.of(sampleAssessment()));

        mockMvc.perform(get("/assessments"))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/assessments"))
                .andExpect(status().isUnauthorized());
    }

    private AssessmentResponse sampleAssessment() {
        return new AssessmentResponse(
                1L,
                "Review",
                "desc",
                AssessmentStatus.DRAFT,
                Instant.parse("2026-01-01T00:00:00Z"),
                new UserResponse(1L, "Admin", "admin@assessment.local", Role.ADMIN)
        );
    }
}