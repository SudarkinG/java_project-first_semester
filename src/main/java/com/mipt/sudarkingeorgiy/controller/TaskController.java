package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.config.RequestScopedBean;
import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Контроллер REST API для задач */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final RequestScopedBean requestScopedBean;

    public TaskController(TaskService taskService, RequestScopedBean requestScopedBean) {
        this.taskService = taskService;
        this.requestScopedBean = requestScopedBean;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok()
                .header("X-Request-Id", requestScopedBean.getRequestId())
                .body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok()
                        .header("X-Request-Id", requestScopedBean.getRequestId())
                        .body(task))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task created = taskService.createTask(task);
        return ResponseEntity.created(URI.create("/api/tasks/" + created.getId()))
                .header("X-Request-Id", requestScopedBean.getRequestId())
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task)
                .map(updated -> ResponseEntity.ok()
                        .header("X-Request-Id", requestScopedBean.getRequestId())
                        .body(updated))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (deleted) {
            return ResponseEntity.noContent()
                    .header("X-Request-Id", requestScopedBean.getRequestId())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

