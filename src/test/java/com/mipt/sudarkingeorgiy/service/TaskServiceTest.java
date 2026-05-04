package com.mipt.sudarkingeorgiy.service;

import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class TaskServiceTest {

    @MockitoBean
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        lenient().when(taskRepository.findAll()).thenReturn(List.of());
    }

    @Test
    @DisplayName("Обновление задачи отметка «выполнено» уходит в репозиторий при сохранении")
    void updateTask_existingTask_updatesCompletedStatus_andVerifiesSaveInteraction() {
        Long id = 10L;
        when(taskRepository.existsById(id)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task patch = sampleTask(id, "Title", false);
        patch.setCompleted(true);

        Task result = taskService.updateTask(id, patch);

        assertThat(result.isCompleted()).isTrue();

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task passedToRepo = captor.getValue();
        assertThat(passedToRepo.getId()).isEqualTo(id);
        assertThat(passedToRepo.isCompleted()).isTrue();
    }

    private Task sampleTask(Long id, String title, boolean completed) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("desc");
        task.setCompleted(completed);
        task.setPriority(Priority.MEDIUM);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setTags(Set.of());
        return task;
    }
}
