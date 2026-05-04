package com.mipt.sudarkingeorgiy.runner;

import com.mipt.sudarkingeorgiy.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class BulkRollbackRunner implements CommandLineRunner {

    private final TaskService taskService;

    public BulkRollbackRunner(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void run(String... args) {
        try {
            taskService.bulkCompleteTasks(List.of(1L, 2L, 999999L)); // 999999 точно не существует
        } catch (Exception e) {
            System.out.println("EXPECTED EXCEPTION: " + e.getClass().getSimpleName());
            System.out.println("MESSAGE: " + e.getMessage());
        }
    }
}
