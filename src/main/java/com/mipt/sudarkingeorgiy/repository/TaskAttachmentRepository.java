package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    @Query("SELECT a FROM TaskAttachment a WHERE a.task.id = :taskId")
    List<TaskAttachment> findByTaskId(Long taskId);
}
