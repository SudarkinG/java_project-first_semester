package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.TaskAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTaskAttachmentRepository {

    private final ConcurrentMap<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0L);

    public TaskAttachment save(TaskAttachment attachment) {
        long id = idSequence.incrementAndGet();
        attachment.setId(id);
        storage.put(id, attachment);
        return attachment;
    }

    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<TaskAttachment> findByTaskId(Long taskId) {
        List<TaskAttachment> result = new ArrayList<>();
        for (TaskAttachment attachment : storage.values()) {
            if (attachment.getTaskId().equals(taskId)) {
                result.add(attachment);
            }
        }
        return result;
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }
}
