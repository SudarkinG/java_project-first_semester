package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.config.ApplicationInfoService;
import com.mipt.sudarkingeorgiy.config.PrototypeScopedBean;
import com.mipt.sudarkingeorgiy.config.RequestScopedBean;
import com.mipt.sudarkingeorgiy.dto.TaskCreateDto;
import com.mipt.sudarkingeorgiy.dto.TaskResponseDto;
import com.mipt.sudarkingeorgiy.exception.GlobalExceptionHandler;
import com.mipt.sudarkingeorgiy.mapper.TaskMapper;
import com.mipt.sudarkingeorgiy.model.Priority;
import com.mipt.sudarkingeorgiy.model.Task;
import com.mipt.sudarkingeorgiy.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @MockitoBean
    private RequestScopedBean requestScopedBean;

    @MockitoBean
    private ApplicationInfoService applicationInfoService;

    @MockitoBean
    private ObjectFactory<PrototypeScopedBean> prototypeScopedBeanFactory;

    @BeforeEach
    void stubScopeBeans() {
        PrototypeScopedBean prototype = org.mockito.Mockito.mock(PrototypeScopedBean.class);
        when(prototype.getId()).thenReturn("test-prototype");
        when(prototypeScopedBeanFactory.getObject()).thenReturn(prototype);
    }

    @Test
    @DisplayName("Создание задачи")
    void createTask_validRequest_returns201() throws Exception {
        when(taskMapper.toEntity(any(TaskCreateDto.class))).thenAnswer(invocation -> {
            TaskCreateDto dto = invocation.getArgument(0);
            Task task = new Task();
            task.setTitle(dto.getTitle());
            task.setDescription(dto.getDescription());
            task.setPriority(dto.getPriority());
            task.setDueDate(dto.getDueDate());
            task.setTags(dto.getTags());
            return task;
        });

        Task saved = new Task();
        saved.setId(42L);
        saved.setTitle("Новая задача");
        saved.setPriority(Priority.HIGH);
        saved.setCompleted(false);
        when(taskService.createTask(any(Task.class))).thenReturn(saved);

        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(42L);
        dto.setTitle("Новая задача");
        dto.setPriority(Priority.HIGH);
        dto.setCompleted(false);
        when(taskMapper.toResponseDto(any(Task.class))).thenReturn(dto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Новая задача","priority":"HIGH"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.title").value("Новая задача"))
                .andExpect(result -> {
                    String location = result.getResponse().getHeader(HttpHeaders.LOCATION);
                    assertThat(location).isNotNull().contains("/api/tasks/42");
                });
    }

    @Test
    @DisplayName("Создание задачи с пустым заголовком")
    void createTask_blankTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","priority":"HIGH"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение задачи по id")
    void getTaskById_existing_returns200() throws Exception {
        Task task = new Task();
        task.setId(5L);
        task.setTitle("Сохранённая задача");
        task.setCompleted(true);
        task.setPriority(Priority.LOW);
        when(taskService.getTaskById(5L)).thenReturn(Optional.of(task));

        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(5L);
        dto.setTitle("Сохранённая задача");
        dto.setCompleted(true);
        dto.setPriority(Priority.LOW);
        when(taskMapper.toResponseDto(task)).thenReturn(dto);

        mockMvc.perform(get("/api/tasks/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Сохранённая задача"))
                .andExpect(jsonPath("$.completed").value(true));
    }
}
