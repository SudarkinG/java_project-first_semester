package com.mipt.sudarkingeorgiy.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.sudarkingeorgiy.dto.ProblemDetailsDto;
import com.mipt.sudarkingeorgiy.dto.TaskRequest;
import com.mipt.sudarkingeorgiy.dto.TaskResponse;
import com.mipt.sudarkingeorgiy.exception.ExternalApiException;
import com.mipt.sudarkingeorgiy.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final int MAX_BODY_LOG_LENGTH = 300;

    private final RestClient externalApiRestClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient externalApiRestClient, ObjectMapper objectMapper) {
        this.externalApiRestClient = externalApiRestClient;
        this.objectMapper = objectMapper;
    }

    public TaskResponse createTask(TaskRequest request) {
        try {
            ResponseEntity<TaskResponse> response = externalApiRestClient.post()
                    .uri("/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(TaskResponse.class);

            if (response.getStatusCode() != HttpStatus.CREATED || response.getHeaders().getLocation() == null) {
                throw new ExternalApiException("External API returned invalid create response");
            }
            return response.getBody();
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout or connection error", ex);
        }
    }

    public TaskResponse getTask(Long id) {
        try {
            return externalApiRestClient.get()
                    .uri("/tasks/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(TaskResponse.class);
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout or connection error", ex);
        }
    }

    public List<TaskResponse> getTasks(Boolean completed, Integer limit) {
        try {
            return externalApiRestClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/tasks");
                        if (completed != null) {
                            builder.queryParam("completed", completed);
                        }
                        if (limit != null) {
                            builder.queryParam("limit", limit);
                        }
                        return builder.build();
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TaskResponse>>() {
                    });
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout or connection error", ex);
        }
    }

    public void deleteTask(Long id) {
        try {
            externalApiRestClient.delete()
                    .uri("/tasks/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout or connection error", ex);
        }
    }

    public Object callUnstable(String mode) {
        try {
            return externalApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/unstable").queryParam("mode", mode).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(Object.class);
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        } catch (ResourceAccessException ex) {
            throw new ExternalApiException("External API timeout or connection error", ex);
        }
    }

    private RuntimeException mapClientException(RestClientResponseException ex) {
        if (ex instanceof HttpClientErrorException.NotFound) {
            String detail = extractProblemDetail(ex);
            return new TaskNotFoundException(detail);
        }
        if (ex instanceof HttpServerErrorException) {
            maybeLogUnexpectedHtml(ex);
            return new ExternalApiException("External API server error: " + ex.getStatusCode().value(), ex);
        }
        if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return new ExternalApiException("External API rate limit exceeded (429)", ex);
        }
        return new ExternalApiException("External API client error: " + ex.getStatusCode().value(), ex);
    }

    private String extractProblemDetail(RestClientResponseException ex) {
        try {
            ProblemDetailsDto dto = objectMapper.readValue(ex.getResponseBodyAsByteArray(), ProblemDetailsDto.class);
            if (dto.detail() != null && !dto.detail().isBlank()) {
                return dto.detail();
            }
        } catch (Exception ignored) {
        }
        return "Task not found in external API";
    }

    private void maybeLogUnexpectedHtml(RestClientResponseException ex) {
        MediaType contentType = ex.getResponseHeaders() != null ? ex.getResponseHeaders().getContentType() : null;
        if (contentType != null && MediaType.TEXT_HTML.isCompatibleWith(contentType)) {
            byte[] body = ex.getResponseBodyAsByteArray();
            String raw = body == null ? "" : new String(body, StandardCharsets.UTF_8);
            String shortened = raw.length() > MAX_BODY_LOG_LENGTH ? raw.substring(0, MAX_BODY_LOG_LENGTH) + "..." : raw;
            log.warn("External API returned HTML body for status={}: {}", ex.getStatusCode().value(), shortened);
        }
    }
}
