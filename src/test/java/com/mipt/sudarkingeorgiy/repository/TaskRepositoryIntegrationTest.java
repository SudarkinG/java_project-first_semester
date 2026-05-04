package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void registerDs(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Поиск по сроку")
    void findDueWithinRange_customQuery_returnsTasksInRange() {
        LocalDate today = LocalDate.now();
        taskRepository.save(newTask("через 3 дня", today.plusDays(3)));
        taskRepository.save(newTask("через 7 дней", today.plusDays(7)));
        taskRepository.save(newTask("через 10 дней", today.plusDays(10)));

        List<Task> result = taskRepository.findDueWithinRange(today, today.plusDays(7));

        assertThat(result).extracting(Task::getTitle)
                .containsExactlyInAnyOrder("через 3 дня", "через 7 дней");
    }

    private Task newTask(String title, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("desc");
        task.setCompleted(false);
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(dueDate);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setTags(Set.of());
        return task;
    }
}
