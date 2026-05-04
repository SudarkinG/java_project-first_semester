package com.mipt.sudarkingeorgiy.service;

import com.mipt.sudarkingeorgiy.dto.TaskPriorityCountDto;
import com.mipt.sudarkingeorgiy.model.Priority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatisticsJdbcService {

    private static final RowMapper<TaskPriorityCountDto> TASK_PRIORITY_COUNT_ROW_MAPPER = (rs, rowNum) ->
            new TaskPriorityCountDto(
                    Priority.valueOf(rs.getString("priority")),
                    rs.getLong("task_count")
            );

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskPriorityCountDto> getTasksCountByPriority() {
        String sql = """
                SELECT priority, COUNT(*) AS task_count
                FROM tasks
                GROUP BY priority
                ORDER BY priority
                """;
        return jdbcTemplate.query(sql, TASK_PRIORITY_COUNT_ROW_MAPPER);
    }
}
