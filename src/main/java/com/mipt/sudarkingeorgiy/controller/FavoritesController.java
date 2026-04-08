package com.mipt.sudarkingeorgiy.controller;

import com.mipt.sudarkingeorgiy.dto.TaskResponseDto;
import com.mipt.sudarkingeorgiy.mapper.TaskMapper;
import com.mipt.sudarkingeorgiy.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Session-based favorite tasks")
public class FavoritesController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public FavoritesController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @Operation(summary = "Add task to favorites")
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long taskId, HttpSession session) {
        Set<Long> favorites = getFavoriteIds(session);
        favorites.add(taskId);
        session.setAttribute("favoriteTaskIds", favorites);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove task from favorites")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long taskId, HttpSession session) {
        Set<Long> favorites = getFavoriteIds(session);
        favorites.remove(taskId);
        session.setAttribute("favoriteTaskIds", favorites);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all favorite tasks")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getFavorites(HttpSession session) {
        Set<Long> favoriteIds = getFavoriteIds(session);
        List<TaskResponseDto> result = favoriteIds.stream()
                .flatMap(id -> taskService.getTaskById(id).stream())
                .map(taskMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @SuppressWarnings("unchecked")
    private Set<Long> getFavoriteIds(HttpSession session) {
        Set<Long> favorites = (Set<Long>) session.getAttribute("favoriteTaskIds");
        if (favorites == null) {
            favorites = new HashSet<>();
        }
        return favorites;
    }
}
