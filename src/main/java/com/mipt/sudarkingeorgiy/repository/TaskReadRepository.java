package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Task;

import java.util.List;
import java.util.Optional;

/** Интерфейс только для чтения задач */
public interface TaskReadRepository {

    List<Task> findAll();

    Optional<Task> findById(Long id);

    boolean existsById(Long id);
}
