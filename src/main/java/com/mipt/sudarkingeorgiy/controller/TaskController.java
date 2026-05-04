package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.config.ApplicationInfoService;
import com.mipt.sudarkingeorgiy.config.PrototypeScopedBean;
import com.mipt.sudarkingeorgiy.config.RequestScopedBean;
import com.mipt.sudarkingeorgiy.dto.TaskCreateDto;
import com.mipt.sudarkingeorgiy.dto.TaskResponseDto;
import com.mipt.sudarkingeorgiy.dto.TaskUpdateDto;
import com.mipt.sudarkingeorgiy.exception.TaskNotFoundException;
import com.mipt.sudarkingeorgiy.mapper.TaskMapper;
import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.service.TaskService;
import com.mipt.sudarkingeorgiy.validation.OnCreate;
import com.mipt.sudarkingeorgiy.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@RestController
@RequestMapping("/api")
@Tag(name = "Tasks", description = "Task management operations")
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
    @Operation(summary = "Get application info", description = "Returns app name, version and scope demo")
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
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "List of tasks returned")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponseDto> body = tasks.stream()
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(body);
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Get task by ID")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
        return ResponseEntity.ok(taskMapper.toResponseDto(task));
    }

    @PostMapping("/tasks")
    @Operation(summary = "Create a new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    public ResponseEntity<TaskResponseDto> createTask(
            @RequestBody @Validated(OnCreate.class) TaskCreateDto createDto) {
        Task toSave = taskMapper.toEntity(createDto);
        Task saved = taskService.createTask(toSave);
        TaskResponseDto body = taskMapper.toResponseDto(saved);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "Update an existing task")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Validated(OnUpdate.class) TaskUpdateDto updateDto) {
        Task existing = taskService.getTaskById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
        taskMapper.updateEntity(updateDto, existing);
        Task updated = taskService.updateTask(id, existing);
        return ResponseEntity.ok(taskMapper.toResponseDto(updated));
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Delete a task")
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskService.deleteTask(id)) {
            throw new TaskNotFoundException("Task not found: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
