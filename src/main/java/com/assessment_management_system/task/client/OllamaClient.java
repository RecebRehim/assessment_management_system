package com.assessment_management_system.task.client;

import com.assessment_management_system.task.config.OllamaProperties;
import com.assessment_management_system.task.exception.AiServiceUnavailableException;
import java.time.Duration;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OllamaClient {

    private final RestClient restClient;
    private final OllamaProperties properties;

    public OllamaClient(OllamaProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(Math.max(1, properties.timeoutSeconds())));
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public String generate(String prompt) {
        try {
            OllamaGenerateResponse response = restClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "model", properties.model(),
                            "prompt", prompt,
                            "stream", false
                    ))
                    .retrieve()
                    .body(OllamaGenerateResponse.class);

            if (response == null || response.response() == null || response.response().isBlank()) {
                throw new AiServiceUnavailableException("LLM returned an empty summary");
            }
            return response.response().trim();
        } catch (AiServiceUnavailableException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new AiServiceUnavailableException(
                    "LLM is unavailable. Score and result status were not changed.",
                    ex
            );
        }
    }

    public record OllamaGenerateResponse(String response) {
    }
}