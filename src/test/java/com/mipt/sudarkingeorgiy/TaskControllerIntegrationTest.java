package com.mipt.sudarkingeorgiy;

import com.mipt.sudarkingeorgiy.dto.TaskResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/** Тесты контроллера задач: GET эндпоинты */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/tasks";
    }

    @Test
    @DisplayName("GET /api/tasks позитивный")
    void getAllTasks_positive_returnsList() {
        ResponseEntity<TaskResponseDto[]> response =
                restTemplate.getForEntity(baseUrl(), TaskResponseDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("GET негативный")
    void getAllTasks_negative_wrongPath_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/task", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET /api/tasks/{id} поз")
    void getTaskById_positive_existingTask_returnsTask() {
        ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity(baseUrl() + "/1", TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("GET нег")
    void getTaskById_negative_notFound_returns404() {
        ResponseEntity<TaskResponseDto> response =
                restTemplate.getForEntity(baseUrl() + "/99999", TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
