package com.assessment_management_system.task;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AssessmentApiIntegrationTest {

    private static final String ADMIN = "admin@assessment.local";
    private static final String ADMIN_PASSWORD = "Admin123!";
    private static final String ANALYST = "analyst@assessment.local";
    private static final String ANALYST_PASSWORD = "Analyst123!";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullAssessmentLifecycleEnforcesValidationSecurityAndBusinessRules() throws Exception {
        mockMvc.perform(post("/assessments")
                        .with(httpBasic(ADMIN, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"description\":\"missing title\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"));

        MvcResult created = mockMvc.perform(post("/assessments")
                        .with(httpBasic(ADMIN, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Access review\",\"description\":\"Quarterly access review\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        Integer id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/assessments")
                        .with(httpBasic(ANALYST, ANALYST_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Should fail\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/assessments/" + id)
                        .with(httpBasic(ADMIN, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Access review\",\"description\":\"Updated\",\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/assessments/" + id)
                        .with(httpBasic(ANALYST, ANALYST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Access review"));

        mockMvc.perform(post("/assessments/" + id + "/result")
                        .with(httpBasic(ADMIN, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":140}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("validation")));

        mockMvc.perform(post("/assessments/" + id + "/result")
                        .with(httpBasic(ADMIN, ADMIN_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"score\":81}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(81))
                .andExpect(jsonPath("$.resultStatus").value("EXCELLENT"));

        mockMvc.perform(get("/assessments/" + id + "/result")
                        .with(httpBasic(ANALYST, ANALYST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(81))
                .andExpect(jsonPath("$.resultStatus").value("EXCELLENT"));
    }
}