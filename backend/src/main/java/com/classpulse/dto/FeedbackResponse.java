package com.classpulse.dto;

import com.classpulse.entity.Sentiment;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long id,
        Long studentId,
        String studentName,
        String note,
        LocalDateTime timestamp,
        Sentiment sentiment
) {
}
