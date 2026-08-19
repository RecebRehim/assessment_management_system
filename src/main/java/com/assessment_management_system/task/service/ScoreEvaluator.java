package com.assessment_management_system.task.service;

import com.assessment_management_system.task.enums.ResultStatus;

public final class ScoreEvaluator {

    private ScoreEvaluator() {
    }

    public static ResultStatus fromScore(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("score must be between 0 and 100");
        }
        if (score < 50) {
            return ResultStatus.FAIL;
        }
        if (score < 80) {
            return ResultStatus.PASS;
        }
        return ResultStatus.EXCELLENT;
    }
}