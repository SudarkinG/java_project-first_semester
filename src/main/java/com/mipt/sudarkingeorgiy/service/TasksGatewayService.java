package com.mipt.sudarkingeorgiy.service;

import com.mipt.sudarkingeorgiy.client.ExternalTasksClient;
import com.mipt.sudarkingeorgiy.dto.TaskRequest;
import com.mipt.sudarkingeorgiy.dto.TaskResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createFallback")
    public TaskResponse createTask(TaskRequest request) {
        return externalTasksClient.createTask(request);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getFallback")
    public TaskResponse getTask(Long id) {
        return externalTasksClient.getTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "listFallback")
    public List<TaskResponse> getTasks(Boolean completed, Integer limit) {
        return externalTasksClient.getTasks(completed, limit);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteFallback")
    public void deleteTask(Long id) {
        externalTasksClient.deleteTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "unstableFallback")
    public Object callUnstable(String mode) {
        return externalTasksClient.callUnstable(mode);
    }

    private TaskResponse createFallback(TaskRequest request, Throwable throwable) {
        return new TaskResponse(-1L, "fallback-task", "External API unavailable", false);
    }

    private TaskResponse getFallback(Long id, Throwable throwable) {
        return new TaskResponse(id, "fallback-task", "External API unavailable", false);
    }

    private List<TaskResponse> listFallback(Boolean completed, Integer limit, Throwable throwable) {
        return List.of(new TaskResponse(-1L, "fallback-task", "External API unavailable", false));
    }

    private void deleteFallback(Long id, Throwable throwable) {
    }

    private Object unstableFallback(String mode, Throwable throwable) {
        return java.util.Map.of(
                "mode", mode,
                "fallback", true,
                "message", "External API unavailable"
        );
    }
}
