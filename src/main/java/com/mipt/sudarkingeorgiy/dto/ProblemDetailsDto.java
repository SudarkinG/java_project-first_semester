package com.mipt.sudarkingeorgiy.dto;

public record ProblemDetailsDto(
        String type,
        String title,
        Integer status,
        String detail
) {
}
