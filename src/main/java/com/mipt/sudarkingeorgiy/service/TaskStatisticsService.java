package com.mipt.sudarkingeorgiy.service;

import com.mipt.sudarkingeorgiy.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/** Сервис для демонстрации @Qualifier */
@Service
public class TaskStatisticsService {

    private final TaskRepository primaryRepository;
    private final TaskRepository stubTaskRepository;

    public TaskStatisticsService(TaskRepository primaryRepository,
                                 @Qualifier("stubTaskRepository") TaskRepository stubTaskRepository) {
        this.primaryRepository = primaryRepository;
        this.stubTaskRepository = stubTaskRepository;
    }

    /** Возвращает имена репозиториев, количество задач и разницу */
    public Map<String, Object> getRepositoryComparison() {
        int primaryCount = primaryRepository.findAll().size();
        int stubCount = stubTaskRepository.findAll().size();
        Map<String, Object> result = new HashMap<>();
        result.put("primaryRepository", "InMemoryTaskRepository");
        result.put("primaryTaskCount", primaryCount);
        result.put("stubRepository", "StubTaskRepository");
        result.put("stubTaskCount", stubCount);
        result.put("difference", primaryCount - stubCount);
        return result;
    }
}

