package com.assessment_management_system.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.assessment_management_system.task.client.OllamaClient;
import com.assessment_management_system.task.dto.AiSummaryResponse;
import com.assessment_management_system.task.entity.Assessment;
import com.assessment_management_system.task.entity.AssessmentResult;
import com.assessment_management_system.task.enums.ResultStatus;
import com.assessment_management_system.task.exception.AiServiceUnavailableException;
import com.assessment_management_system.task.repository.AssessmentResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AiSummaryServiceTest {

    @Mock
    private AssessmentResultService resultService;

    @Mock
    private AssessmentResultRepository resultRepository;

    @Mock
    private OllamaClient ollamaClient;

    @InjectMocks
    private AiSummaryService aiSummaryService;

    private AssessmentResult result;

    @BeforeEach
    void setUp() {
        Assessment assessment = new Assessment();
        assessment.setId(4L);
        assessment.setTitle("Security review");
        assessment.setDescription("Access control checks");

        result = new AssessmentResult();
        result.setId(9L);
        result.setAssessment(assessment);
        result.setScore(81);
        result.setResultStatus(ResultStatus.EXCELLENT);
    }

    @Test
    void generateSummaryStoresTextWithoutChangingScoreOrStatus() {
        when(resultService.getByAssessmentId(4L)).thenReturn(result);
        when(ollamaClient.generate(ArgumentMatchers.anyString())).thenReturn("Strong controls were observed.");
        when(resultRepository.save(result)).thenReturn(result);

        AiSummaryResponse response = aiSummaryService.generateSummary(4L);

        assertThat(response.score()).isEqualTo(81);
        assertThat(response.resultStatus()).isEqualTo("EXCELLENT");
        assertThat(response.summary()).isEqualTo("Strong controls were observed.");
        assertThat(result.getScore()).isEqualTo(81);
        assertThat(result.getResultStatus()).isEqualTo(ResultStatus.EXCELLENT);
    }

    @Test
    void llmFailureDoesNotChangeBusinessResult() {
        when(resultService.getByAssessmentId(4L)).thenReturn(result);
        when(ollamaClient.generate(ArgumentMatchers.anyString()))
                .thenThrow(new AiServiceUnavailableException("LLM down"));

        assertThatThrownBy(() -> aiSummaryService.generateSummary(4L))
                .isInstanceOf(AiServiceUnavailableException.class);

        assertThat(result.getScore()).isEqualTo(81);
        assertThat(result.getResultStatus()).isEqualTo(ResultStatus.EXCELLENT);
        assertThat(result.getAiSummary()).isNull();
        verify(resultRepository, never()).save(any());
    }
}