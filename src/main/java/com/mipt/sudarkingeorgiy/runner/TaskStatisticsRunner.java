package com.mipt.sudarkingeorgiy.runner;

import com.mipt.sudarkingeorgiy.dto.TaskPriorityCountDto;
import com.mipt.sudarkingeorgiy.service.TaskStatisticsJdbcService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
public class TaskStatisticsRunner implements CommandLineRunner {

    private final TaskStatisticsJdbcService taskStatisticsJdbcService;

    public TaskStatisticsRunner(TaskStatisticsJdbcService taskStatisticsJdbcService) {
        this.taskStatisticsJdbcService = taskStatisticsJdbcService;
    }

    @Override
    public void run(String... args) {
        List<TaskPriorityCountDto> stats = taskStatisticsJdbcService.getTasksCountByPriority();

        for (TaskPriorityCountDto item : stats) {
            System.out.println(item.priority() + " = " + item.count());
        }
    }
}