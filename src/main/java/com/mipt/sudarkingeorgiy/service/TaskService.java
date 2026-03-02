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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Сервис задач
 *  Репозиторий передаётся в конструктор */
@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    /** Кэш задач в памяти */
    private final Map<String, Task> taskCache = new ConcurrentHashMap<>();

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /** При старте приложения загружаем задачи из репозитория в кэш*/
    @PostConstruct
    public void initCache() {
        List<Task> tasks = taskRepository.findAll();
        for (Task task : tasks) {
            if (task.getId() != null) {
                taskCache.put(task.getId().toString(), task);
            }
        }
        log.info("[TaskService] Cache initialized with {} tasks from repository", taskCache.size());
    }

    /** Перед остановкой логируем размер кэша и сохраняем статистику в файл */
    @PreDestroy
    public void cleanup() {
        int count = taskCache.size();
        log.info("[TaskService] PreDestroy: clearing cache, current task count = {}", count);
        try {
            Path statsFile = Path.of("task-service-stats.txt");
            String content = String.format("TaskService shutdown stats%ntasks in cache: %d%n", count);
            Files.writeString(statsFile, content);
            log.info("[TaskService] Statistics saved to {}", statsFile.toAbsolutePath());
        } catch (IOException e) {
            log.error("[TaskService] Failed to save statistics to file", e);
        }
        taskCache.clear();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        Task saved = taskRepository.save(task);
        if (saved.getId() != null) {
            taskCache.put(saved.getId().toString(), saved);
        }
        return saved;
    }

    public Optional<Task> updateTask(Long id, Task task) {
        Task updated = taskRepository.update(id, task);
        if (updated == null) {
            return Optional.empty();
        }
        taskCache.put(id.toString(), updated);
        return Optional.of(updated);
    }

    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        taskCache.remove(id.toString());
        return true;
    }
}
