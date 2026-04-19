package com.mipt.sudarkingeorgiy.external;

import com.mipt.sudarkingeorgiy.dto.TaskPatchRequest;
import com.mipt.sudarkingeorgiy.dto.TaskRequest;
import com.mipt.sudarkingeorgiy.dto.TaskResponse;
import com.mipt.sudarkingeorgiy.exception.TaskNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, TaskResponse> tasks = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        long id = ids.incrementAndGet();
        TaskResponse created = new TaskResponse(id, request.title(), request.description(), request.completed());
        tasks.put(id, created);
        return ResponseEntity
                .created(URI.create("/external/v1/tasks/" + id))
                .body(created);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse getTask(@PathVariable Long id) {
        TaskResponse task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException("External task not found: " + id);
        }
        return task;
    }

    @PutMapping(value = "/tasks/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskResponse> putTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("External task not found: " + id);
        }
        TaskResponse updated = new TaskResponse(id, request.title(), request.description(), request.completed());
        tasks.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping(value = "/tasks/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskResponse> patchTask(@PathVariable Long id, @Valid @RequestBody TaskPatchRequest request) {
        TaskResponse existing = tasks.get(id);
        if (existing == null) {
            throw new TaskNotFoundException("External task not found: " + id);
        }
        boolean completed = request.completed() != null ? request.completed() : existing.completed();
        TaskResponse patched = new TaskResponse(existing.id(), existing.title(), existing.description(), completed);
        tasks.put(id, patched);
        return ResponseEntity.ok(patched);
    }

    @GetMapping("/tasks")
    public ResponseEntity<java.util.List<TaskResponse>> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit
    ) {
        var result = tasks.values().stream()
                .filter(t -> completed == null || t.completed() == completed)
                .sorted(Comparator.comparing(TaskResponse::id))
                .toList();

        if (limit != null && limit >= 0 && result.size() > limit) {
            return ResponseEntity.ok(new ArrayList<>(result.subList(0, limit)));
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        TaskResponse removed = tasks.remove(id);
        if (removed == null) {
            throw new TaskNotFoundException("External task not found: " + id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(Duration.ofSeconds(5));
                yield ResponseEntity.ok(Map.of("mode", "timeout", "status", "late-response"));
            }
            case "500" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "simulated-500"));
            case "429" -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "5")
                    .body(Map.of("error", "simulated-429"));
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body>Bad gateway html response</body></html>");
            default -> {
                ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                detail.setDetail("Unknown mode: " + mode);
                yield ResponseEntity.badRequest().body(detail);
            }
        };
    }
}
