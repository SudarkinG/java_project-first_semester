package com.mipt.sudarkingeorgiy.repository;

import com.mipt.sudarkingeorgiy.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

    private final ConcurrentMap<Long, TaskAttachment> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0L);

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        long id = idSequence.incrementAndGet();
        attachment.setId(id);
        storage.put(id, attachment);
        return attachment;
    }

    @Override
    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TaskAttachment> findByTaskId(Long taskId) {
        List<TaskAttachment> result = new ArrayList<>();
        for (TaskAttachment attachment : storage.values()) {
            if (attachment.getTaskId().equals(taskId)) {
                result.add(attachment);
            }
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
