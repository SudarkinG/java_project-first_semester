package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Task;

/** Полный репозиторий задач (CRUD). Наследует чтение, добавляет запись и удаление */
public interface TaskRepository extends TaskReadRepository {

    Task save(Task task);

    Task update(Long id, Task task);

    void deleteById(Long id);
}
