package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Task;

import java.util.List;
import java.util.Optional;

/** Интерфейс репозитория задач (CRUD) */
public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(Long id);

    Task save(Task task);

    Task update(Long id, Task task);

    void deleteById(Long id);

    boolean existsById(Long id);
}

