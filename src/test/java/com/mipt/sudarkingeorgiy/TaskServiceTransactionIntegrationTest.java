package com.mipt.sudarkingeorgiy;

import com.mipt.sudarkingeorgiy.exception.TaskBulkCompleteException;
import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.repository.TaskRepository;
import com.mipt.sudarkingeorgiy.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTransactionIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("bulkCompleteTasks rolls back when one id is missing")
    void bulkCompleteTasks_rollsBackWhenAnyTaskMissing() {
        Task t1 = taskRepository.save(newTask("rollback-1"));
        Task t2 = taskRepository.save(newTask("rollback-2"));

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(t1.getId(), t2.getId(), 999_999L)))
                .isInstanceOf(TaskBulkCompleteException.class);

        Task reloaded1 = taskRepository.findById(t1.getId()).orElseThrow();
        Task reloaded2 = taskRepository.findById(t2.getId()).orElseThrow();
        assertThat(reloaded1.isCompleted()).isFalse();
        assertThat(reloaded2.isCompleted()).isFalse();
    }

    private Task newTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("transaction test");
        task.setCompleted(false);
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDate.now().plusDays(2));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setTags(Set.of());
        return task;
    }
}
