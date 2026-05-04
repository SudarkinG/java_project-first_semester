package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Заглушка только для чтения. Реализует TaskReadRepository (ISP) */
public class StubTaskRepository implements TaskReadRepository {

    private final List<Task> tasks;

    public StubTaskRepository() {
        List<Task> initial = new ArrayList<>();
        initial.add(new Task(
                1L,
                "Stub task 1",
                "Предопределённая задача 1",
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDate.now().plusDays(7),
                Priority.MEDIUM,
                Set.of()));
        initial.add(new Task(
                2L,
                "Stub task 2",
                "Предопределённая задача 2",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDate.now().plusDays(14),
                Priority.LOW,
                Set.of("demo")));
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
    public boolean existsById(Long id) {
        return tasks.stream().anyMatch(t -> t.getId().equals(id));
    }
}
