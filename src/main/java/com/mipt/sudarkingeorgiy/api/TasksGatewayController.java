package com.mipt.sudarkingeorgiy.api;

import com.mipt.sudarkingeorgiy.dto.TaskRequest;
import com.mipt.sudarkingeorgiy.dto.TaskResponse;
import com.mipt.sudarkingeorgiy.service.TasksGatewayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {

    private final TasksGatewayService tasksGatewayService;

    public TasksGatewayController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse created = tasksGatewayService.createTask(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/v1/tasks/" + created.id())
                .body(created);
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id) {
        return tasksGatewayService.getTask(id);
    }

    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit
    ) {
        return tasksGatewayService.getTasks(completed, limit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        tasksGatewayService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public Object unstable(@RequestParam String mode) {
        return tasksGatewayService.callUnstable(mode);
    }
}
