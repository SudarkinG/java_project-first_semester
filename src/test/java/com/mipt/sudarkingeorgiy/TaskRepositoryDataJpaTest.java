package com.mipt.sudarkingeorgiy;

import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.model.TaskAttachment;
import com.mipt.sudarkingeorgiy.repository.TaskAttachmentRepository;
import com.mipt.sudarkingeorgiy.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryDataJpaTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Test
    @DisplayName("findByCompletedAndPriority returns matching tasks")
    void findByCompletedAndPriority_returnsMatchingTasks() {
        taskRepository.save(newTask("done-high", true, Priority.HIGH, LocalDate.now().plusDays(1)));
        taskRepository.save(newTask("todo-low", false, Priority.LOW, LocalDate.now().plusDays(2)));
        taskRepository.save(newTask("todo-high", false, Priority.HIGH, LocalDate.now().plusDays(3)));

        List<Task> result = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("todo-high");
    }

    @Test
    @DisplayName("@Query due-date range returns tasks for next 7 days")
    void findDueWithinRange_returnsTasksForSevenDaysWindow() {
        LocalDate today = LocalDate.now();
        taskRepository.save(newTask("inside-3d", false, Priority.MEDIUM, today.plusDays(3)));
        taskRepository.save(newTask("inside-7d", false, Priority.MEDIUM, today.plusDays(7)));
        taskRepository.save(newTask("outside-10d", false, Priority.MEDIUM, today.plusDays(10)));

        List<Task> result = taskRepository.findDueWithinRange(today, today.plusDays(7));

        assertThat(result).extracting(Task::getTitle)
                .containsExactly("inside-3d", "inside-7d");
    }

    @Test
    @DisplayName("Task + attachment relation is persisted and queryable")
    void saveTaskWithAttachment_relationIsPersisted() {
        Task task = taskRepository.save(newTask("task-with-file", false, Priority.LOW, LocalDate.now().plusDays(1)));

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("a.txt");
        attachment.setStoredFileName("stored-a.txt");
        attachment.setContentType("text/plain");
        attachment.setSize(12L);
        attachment.setUploadedAt(LocalDateTime.now());
        taskAttachmentRepository.save(attachment);

        List<TaskAttachment> attachments = taskAttachmentRepository.findByTaskId(task.getId());
        List<Task> tasksWithAttachments = taskRepository.findAllWithAttachments();

        assertThat(attachments).hasSize(1);
        assertThat(attachments.get(0).getTaskId()).isEqualTo(task.getId());
        assertThat(tasksWithAttachments).anyMatch(t -> t.getId().equals(task.getId()));
    }

    private Task newTask(String title, boolean completed, Priority priority, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("test");
        task.setCompleted(completed);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setTags(Set.of());
        return task;
    }
}
