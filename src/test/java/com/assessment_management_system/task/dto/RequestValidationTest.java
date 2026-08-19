package com.assessment_management_system.task.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userEmailMustNotBeBlank() {
        CreateUserRequest request = new CreateUserRequest("Ada", "  ", "password1", null);
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email"));
        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("role"));
    }

    @Test
    void assessmentTitleMustNotBeBlank() {
        CreateAssessmentRequest request = new CreateAssessmentRequest(" ", "ok");
        Set<ConstraintViolation<CreateAssessmentRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    void scoreMustStayWithinZeroToOneHundred() {
        assertThat(validator.validate(new CreateResultRequest(-1)))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("score"));
        assertThat(validator.validate(new CreateResultRequest(101)))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("score"));
        assertThat(validator.validate(new CreateResultRequest(null)))
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("score"));
        assertThat(validator.validate(new CreateResultRequest(100))).isEmpty();
    }
}