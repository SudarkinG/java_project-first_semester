package com.mipt.sudarkingeorgiy.config;

import com.mipt.sudarkingeorgiy.repository.StubTaskRepository;
import com.mipt.sudarkingeorgiy.repository.TaskReadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Создаём бин заглушки (только чтение) через @Bean */
@Configuration
public class RepositoryConfig {

    @Bean(name = "stubTaskRepository")
    public TaskReadRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
