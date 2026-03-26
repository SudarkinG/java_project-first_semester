package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.config.ApplicationInfoService;
import com.mipt.sudarkingeorgiy.config.PrototypeScopedBean;
import com.mipt.sudarkingeorgiy.config.RequestScopedBean;
import com.mipt.sudarkingeorgiy.dto.TaskCreateDto;
import com.mipt.sudarkingeorgiy.dto.TaskResponseDto;
import com.mipt.sudarkingeorgiy.dto.TaskUpdateDto;
import com.mipt.sudarkingeorgiy.mapper.TaskMapper;
import com.mipt.sudarkingeorgiy.model.Task;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Контроллер REST API для задач и демонстрации @Value/scope */
@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final RequestScopedBean requestScopedBean;
    private final ApplicationInfoService applicationInfoService;
    private final ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory;

    public TaskController(
            TaskService taskService,
            TaskMapper taskMapper,
            RequestScopedBean requestScopedBean,
            ApplicationInfoService applicationInfoService,
            ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
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
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<TaskResponseDto> body = taskService.getAllTasks().stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-Request-Id", requestScopedBean.getRequestId())
                .body(body);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(taskMapper::toResponseDto)
                .map(dto -> ResponseEntity.ok()
                        .header("X-Request-Id", requestScopedBean.getRequestId())
                        .body(dto))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskCreateDto createDto) {
        Task toSave = taskMapper.toEntity(createDto);
        Task saved = taskService.createTask(toSave);
        TaskResponseDto body = taskMapper.toResponseDto(saved);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location)
                .header("X-Request-Id", requestScopedBean.getRequestId())
                .body(body);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id, @RequestBody TaskUpdateDto updateDto) {
        return taskService.getTaskById(id)
                .map(existing -> {
                    taskMapper.updateEntity(updateDto, existing);
                    Task updated = taskService.updateTask(id, existing);
                    if (updated == null) {
                        return ResponseEntity.<TaskResponseDto>status(HttpStatus.NOT_FOUND).build();
                    }
                    return ResponseEntity.ok()
                            .header("X-Request-Id", requestScopedBean.getRequestId())
                            .body(taskMapper.toResponseDto(updated));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskService.deleteTask(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent()
                .header("X-Request-Id", requestScopedBean.getRequestId())
                .build();
    }
}
