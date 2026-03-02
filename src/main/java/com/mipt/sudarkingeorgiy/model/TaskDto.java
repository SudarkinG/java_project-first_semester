package com.mipt.sudarkingeorgiy.model;

/** DTO для передачи задачи через API, отдельно от сущности слоя данных */
public class TaskDto {

    private Long id;
    private String title;
    private String description;
    private boolean completed;

    public TaskDto() {
    }

    public TaskDto(Long id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public static TaskDto fromTask(Task task) {
        if (task == null) return null;
        return new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }

    public static Task toTask(TaskDto dto) {
        if (dto == null) return null;
        return new Task(dto.getId(), dto.getTitle(), dto.getDescription(), dto.isCompleted());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
