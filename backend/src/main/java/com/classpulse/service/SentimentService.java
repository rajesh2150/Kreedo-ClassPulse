package com.classpulse.service;

import com.classpulse.entity.Sentiment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentimentService {

    private static final List<String> POSITIVE_WORDS = List.of(
            "excellent", "great", "good", "strong", "improved", "improvement",
            "progress", "outstanding", "helpful", "engaged", "confident",
            "successful", "creative", "well done", "amazing", "participated",
            "participation", "active", "focused", "positive", "kind", "respectful"
    );

    private static final List<String> NEGATIVE_WORDS = List.of(
            "poor", "weak", "struggling", "struggled", "confused", "confusion",
            "difficult", "difficulty", "late", "missed", "incomplete",
            "needs improvement", "problem", "issue", "disruptive", "disengaged",
            "failed", "unprepared", "slow", "distracted", "absent", "unsure"
    );

    private static final List<String> NEUTRAL_WORDS = List.of(
            "normal", "okay", "average", "completed", "submitted", "present",
            "reviewed", "attended", "class", "assignment", "topic"
    );

    public Sentiment classify(String text) {
        if (text == null || text.isBlank()) {
            return Sentiment.NEUTRAL;
        }

        String normalized = text.toLowerCase();
        int score = 0;

        for (String word : POSITIVE_WORDS) {
            if (normalized.contains(word)) {
                score += 2;
            }
        }

        for (String word : NEGATIVE_WORDS) {
            if (normalized.contains(word)) {
                score -= 2;
            }
        }

        if (score > 1) {
            return Sentiment.POSITIVE;
        }
        if (score < -1) {
            return Sentiment.NEGATIVE;
        }
        return Sentiment.NEUTRAL;
    }
}
