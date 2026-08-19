package com.assessment_management_system.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.assessment_management_system.task.enums.ResultStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ScoreEvaluatorTest {

    @ParameterizedTest
    @CsvSource({
            "0, FAIL",
            "49, FAIL",
            "50, PASS",
            "79, PASS",
            "80, EXCELLENT",
            "100, EXCELLENT"
    })
    void mapsScoreToBusinessStatus(int score, ResultStatus expected) {
        assertThat(ScoreEvaluator.fromScore(score)).isEqualTo(expected);
    }

    @Test
    void rejectsScoreOutsideAllowedRange() {
        assertThatThrownBy(() -> ScoreEvaluator.fromScore(-1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ScoreEvaluator.fromScore(101))
                .isInstanceOf(IllegalArgumentException.class);
    }
}