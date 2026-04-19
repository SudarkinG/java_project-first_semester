package com.mipt.sudarkingeorgiy.dto;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed
) {
}
