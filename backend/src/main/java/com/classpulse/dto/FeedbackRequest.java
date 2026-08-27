package com.classpulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotBlank(message = "Feedback note is required")
        @Size(max = 1000, message = "Feedback note must be at most 1000 characters")
        String note
) {
}
