package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Заглушка репозитория. Возвращает две предзаполненные задачи, запись и удаление не поддерживает */
public class StubTaskRepository implements TaskRepository {

    private final List<Task> tasks;

    public StubTaskRepository() {
        List<Task> initial = new ArrayList<>();
        initial.add(new Task(1L, "Stub task 1", "Предопределённая задача 1", false));
        initial.add(new Task(2L, "Stub task 2", "Предопределённая задача 2", true));
        this.tasks = Collections.unmodifiableList(initial);
    }

    @Override
    public List<Task> findAll() {
        return tasks;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    @Override
    public Task save(Task task) {
        throw new UnsupportedOperationException("StubTaskRepository is read-only");
    }

    @Override
    public Task update(Long id, Task task) {
        throw new UnsupportedOperationException("StubTaskRepository is read-only");
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("StubTaskRepository is read-only");
    }

    @Override
    public boolean existsById(Long id) {
        return tasks.stream().anyMatch(t -> t.getId().equals(id));
    }
}

