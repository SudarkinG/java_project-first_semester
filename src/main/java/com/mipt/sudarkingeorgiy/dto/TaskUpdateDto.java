package com.mipt.sudarkingeorgiy.dto;

import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.validation.DueDateNotBeforeCreation;
import com.mipt.sudarkingeorgiy.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO for updating an existing task")
public class TaskUpdateDto {

    @Size(min = 3, max = 100, groups = OnUpdate.class, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, groups = OnUpdate.class, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean completed;

    @DueDateNotBeforeCreation(groups = OnUpdate.class)
    private LocalDate dueDate;

    private Priority priority;

    @Size(max = 5, groups = OnUpdate.class, message = "No more than 5 tags allowed")
    private Set<String> tags;

    public TaskUpdateDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
