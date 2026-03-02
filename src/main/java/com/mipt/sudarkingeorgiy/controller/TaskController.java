package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.config.ApplicationInfoService;
import com.mipt.sudarkingeorgiy.config.PrototypeScopedBean;
import com.mipt.sudarkingeorgiy.config.RequestScopedBean;
import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.model.TaskDto;
import com.mipt.sudarkingeorgiy.service.TaskService;
import org.springframework.beans.factory.ObjectFactory;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Контроллер REST API для задач и демонстрации @Value/scope */
@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final RequestScopedBean requestScopedBean;
    private final ApplicationInfoService applicationInfoService;
    private final ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory;

    public TaskController(TaskService taskService, RequestScopedBean requestScopedBean,
                          ApplicationInfoService applicationInfoService,
                          ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory) {
        this.taskService = taskService;
        this.requestScopedBean = requestScopedBean;
        this.applicationInfoService = applicationInfoService;
        this.prototypeScopedBeanFactory = prototypeScopedBeanFactory;
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("appName", applicationInfoService.getAppName());
        result.put("appVersion", applicationInfoService.getAppVersion());
        result.put("requestId", requestScopedBean.getRequestId());
        result.put("requestStartedAt", requestScopedBean.getStartedAt().toString());
        result.put("prototypeId", prototypeScopedBeanFactory.getObject().getId());
        return result;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskDto> dtos = tasks.stream().map(TaskDto::fromTask).toList();
        return ResponseEntity.ok()
                .header("X-Request-Id", requestScopedBean.getRequestId())
                .body(dtos);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok()
                        .header("X-Request-Id", requestScopedBean.getRequestId())
                        .body(TaskDto.fromTask(task)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto dto) {
        Task created = taskService.createTask(TaskDto.toTask(dto));
        return ResponseEntity.created(URI.create("/api/tasks/" + created.getId()))
                .header("X-Request-Id", requestScopedBean.getRequestId())
                .body(TaskDto.fromTask(created));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody TaskDto dto) {
        return taskService.updateTask(id, TaskDto.toTask(dto))
                .map(updated -> ResponseEntity.ok()
                        .header("X-Request-Id", requestScopedBean.getRequestId())
                        .body(TaskDto.fromTask(updated)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/tasks/{id}")
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

