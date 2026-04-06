package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/** Репозиторий задач в памяти. Помечен @Primary */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0L);

    @PostConstruct
    void seedDemoTasks() {
        if (!storage.isEmpty()) {
            return;
        }
        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("Stub task 1");
        t1.setDescription("Предопределённая задача 1");
        t1.setCompleted(false);
        t1.setCreatedAt(LocalDateTime.now());
        t1.setUpdatedAt(LocalDateTime.now());
        t1.setDueDate(LocalDate.now().plusDays(7));
        t1.setPriority(Priority.MEDIUM);
        t1.setTags(Set.of());
        storage.put(1L, t1);

        Task t2 = new Task();
        t2.setId(2L);
        t2.setTitle("Stub task 2");
        t2.setDescription("Предопределённая задача 2");
        t2.setCompleted(true);
        t2.setCreatedAt(LocalDateTime.now());
        t2.setUpdatedAt(LocalDateTime.now());
        t2.setDueDate(LocalDate.now().plusDays(14));
        t2.setPriority(Priority.LOW);
        t2.setTags(Set.of("demo"));
        storage.put(2L, t2);

        idSequence.set(2L);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Task save(Task task) {
        long id = idSequence.incrementAndGet();
        task.setId(id);
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        if (task.getUpdatedAt() == null) {
            task.setUpdatedAt(LocalDateTime.now());
        }
        storage.put(id, task);
        return task;
    }

    @Override
    public Task update(Long id, Task task) {
        if (!storage.containsKey(id)) {
            return null;
        }
        task.setId(id);
        task.setUpdatedAt(LocalDateTime.now());
        storage.put(id, task);
        return task;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}
