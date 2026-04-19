package com.mipt.sudarkingeorgiy.exception;

import jakarta.servlet.http.HttpServletRequest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleTaskNotFound(TaskNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Task not found");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://example.local/errors/task-not-found"));
        return detail;
    }

    @ExceptionHandler(ExternalApiException.class)
    public ProblemDetail handleExternalApiException(ExternalApiException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        detail.setTitle("External API error");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://example.local/errors/external-api"));
        return detail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Internal server error");
        detail.setDetail("Unexpected error on path: " + request.getRequestURI());
        detail.setType(URI.create("https://example.local/errors/internal"));
        return detail;
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ProblemDetail handleRateLimit(RequestNotPermitted ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        detail.setTitle("Rate limit exceeded");
        detail.setDetail("Too many requests to external API");
        detail.setType(URI.create("https://example.local/errors/rate-limit"));
        return detail;
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ProblemDetail handleCircuitOpen(CallNotPermittedException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        detail.setTitle("Circuit breaker open");
        detail.setDetail("External API temporarily unavailable");
        detail.setType(URI.create("https://example.local/errors/circuit-open"));
        return detail;
    }
}
