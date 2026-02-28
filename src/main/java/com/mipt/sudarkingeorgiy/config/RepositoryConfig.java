package com.mipt.sudarkingeorgiy.config;

import com.mipt.sudarkingeorgiy.repository.StubTaskRepository;
import com.mipt.sudarkingeorgiy.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Создаём бин заглушки репозитория через @Bean */
@Configuration
public class RepositoryConfig {

    @Bean(name = "stubTaskRepository")
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}

