package com.mipt.sudarkingeorgiy.dto;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank String title,
        String description,
        boolean completed
) {
}
