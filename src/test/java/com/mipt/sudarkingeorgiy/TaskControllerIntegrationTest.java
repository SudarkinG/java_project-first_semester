package com.mipt.sudarkingeorgiy;

import com.mipt.sudarkingeorgiy.dto.TaskResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/tasks";
    }

    @Test
    @DisplayName("GET /api/tasks поз")
    void getAllTasks_positive_returnsList() {
        ResponseEntity<TaskResponseDto[]> response =
                restTemplate.getForEntity(baseUrl(), TaskResponseDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isNotNull();
    }

    @Test
    @DisplayName("GET /api/tasks нег")
    void getAllTasks_negative_wrongPath_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/task", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET /api/tasks/{id} поз")
    void getTaskById_positive_existingTask_returnsTask() {
        ResponseEntity<TaskResponseDto> response =
                restTemplate.getForEntity(baseUrl() + "/1", TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("GET /api/tasks/{id} нег")
    void getTaskById_negative_notFound_returns404() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl() + "/99999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /api/tasks поз")
    void createTask_positive_returns201() {
        String json = """
                {"title": "Test task", "description": "Desc", "priority": "HIGH"}
                """;
        HttpEntity<String> entity = new HttpEntity<>(json, jsonHeaders());
        ResponseEntity<TaskResponseDto> response =
                restTemplate.postForEntity(baseUrl(), entity, TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test task");
        assertThat(response.getHeaders().getLocation()).isNotNull();
    }

    @Test
    @DisplayName("POST /api/tasks нег blank title")
    void createTask_negative_blankTitle_returns400() {
        String json = """
                {"title": "", "priority": "HIGH"}
                """;
        HttpEntity<String> entity = new HttpEntity<>(json, jsonHeaders());
        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /api/tasks нег no priority")
    void createTask_negative_missingPriority_returns400() {
        String json = """
                {"title": "Valid title"}
                """;
        HttpEntity<String> entity = new HttpEntity<>(json, jsonHeaders());
        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("POST /api/tasks нег short title")
    void createTask_negative_titleTooShort_returns400() {
        String json = """
                {"title": "ab", "priority": "LOW"}
                """;
        HttpEntity<String> entity = new HttpEntity<>(json, jsonHeaders());
        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} поз")
    void updateTask_positive_returns200() {
        String json = """
                {"title": "Updated title"}
                """;
        HttpEntity<String> entity = new HttpEntity<>(json, jsonHeaders());
        ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                baseUrl() + "/1", HttpMethod.PUT, entity, TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Updated title");
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} нег")
    void updateTask_negative_notFound_returns404() {
        String json = """
                {"title": "Updated"}
                """;
        HttpEntity<String> entity = new HttpEntity<>(json, jsonHeaders());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/99999", HttpMethod.PUT, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} pos")
    void deleteTask_positive_returns204() {
        String json = """
                {"title": "To delete", "priority": "LOW"}
                """;
        HttpEntity<String> createEntity = new HttpEntity<>(json, jsonHeaders());
        ResponseEntity<TaskResponseDto> created =
                restTemplate.postForEntity(baseUrl(), createEntity, TaskResponseDto.class);
        Long id = created.getBody().getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} neg")
    void deleteTask_negative_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/99999", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
