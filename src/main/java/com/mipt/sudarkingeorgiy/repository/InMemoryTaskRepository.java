package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/** Репозиторий задач в памяти. Помечен @Primary */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final ConcurrentMap<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0L);

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
        storage.put(id, task);
        return task;
    }

    @Override
    public Task update(Long id, Task task) {
        if (!storage.containsKey(id)) {
            return null;
        }
        task.setId(id);
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

