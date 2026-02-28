package com.mipt.sudarkingeorgiy;

import com.mipt.sudarkingeorgiy.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/** Тесты контроллера задач: каждый эндпоинт проверяется позитивно и негативно */
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
        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl(), Task[].class);
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
        Task created = restTemplate.postForObject(baseUrl(),
                new Task(null, "Test", "Desc", false), Task.class);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();

        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl() + "/" + created.getId(), Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test");
    }

    @Test
    @DisplayName("GET нег")
    void getTaskById_negative_notFound_returns404() {
        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl() + "/99999", Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /api/tasks поз")
    void createTask_positive_returnsCreated() {
        Task task = new Task(null, "New Task", "Description", false);
        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl(), task, Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("New Task");
    }

    @Test
    @DisplayName("POST нег")
    void createTask_negative_wrongUrl_returns404() {
        Task task = new Task(null, "New", "Desc", false);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/task", task, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} поз")
    void updateTask_positive_existingTask_returnsOk() {
        Task created = restTemplate.postForObject(baseUrl(),
                new Task(null, "Original", "Desc", false), Task.class);
        assertThat(created).isNotNull();

        Task update = new Task(created.getId(), "Updated", "New desc", true);
        ResponseEntity<Task> response = restTemplate.exchange(
                baseUrl() + "/" + created.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(update),
                Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Updated");
        assertThat(response.getBody().isCompleted()).isTrue();
    }

    @Test
    @DisplayName("PUT нег")
    void updateTask_negative_notFound_returns404() {
        Task update = new Task(99999L, "Updated", "Desc", true);
        ResponseEntity<Task> response = restTemplate.exchange(
                baseUrl() + "/99999",
                HttpMethod.PUT,
                new HttpEntity<>(update),
                Task.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE api/tasks/{id} поз")
    void deleteTask_positive_existingTask_returnsNoContent() {
        Task created = restTemplate.postForObject(baseUrl(),
                new Task(null, "To Delete", "Desc", false), Task.class);
        assertThat(created).isNotNull();

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/" + created.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Task> getAfter = restTemplate.getForEntity(baseUrl() + "/" + created.getId(), Task.class);
        assertThat(getAfter.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE нег")
    void deleteTask_negative_notFound_returns404() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/99999",
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
