package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("""
            SELECT t FROM Task t
            WHERE t.dueDate IS NOT NULL
              AND t.dueDate BETWEEN :today AND :endDate
            ORDER BY t.dueDate ASC
            """)
    List<Task> findDueWithinRange(@Param("today") LocalDate today, @Param("endDate") LocalDate endDate);
}
