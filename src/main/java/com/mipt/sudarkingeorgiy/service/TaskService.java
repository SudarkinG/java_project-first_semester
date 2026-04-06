package com.mipt.sudarkingeorgiy.service;

import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/** Сервис задач. Репозиторий передаётся в конструктор */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void initCache() {
        int count = taskRepository.findAll().size();
        log.info("[TaskService] Initialized, tasks in repository: {}", count);
    }

    @PreDestroy
    public void cleanup() {
        int count = taskRepository.findAll().size();
        log.info("[TaskService] PreDestroy: task count = {}", count);
        try {
            Path statsFile = Path.of("task-service-stats.txt");
            String content = String.format("TaskService shutdown stats%ntasks: %d%n", count);
            Files.writeString(statsFile, content);
            log.info("[TaskService] Statistics saved to {}", statsFile.toAbsolutePath());
        } catch (IOException e) {
            log.error("[TaskService] Failed to save statistics to file", e);
        }
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task task) {
        if (!taskRepository.existsById(id)) {
            return null;
        }
        task.setId(id);
        return taskRepository.save(task);
    }

    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }
}
