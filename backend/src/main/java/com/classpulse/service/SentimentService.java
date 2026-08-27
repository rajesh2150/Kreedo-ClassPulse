package com.classpulse.service;

import com.classpulse.entity.Sentiment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SentimentService {

    private final RestClient restClient;
    private final String groqApiKey;
    private final String groqModel;

    public SentimentService(@Value("${groq.api.key:}") String groqApiKey,
                           @Value("${groq.api.model:llama-3.1-8b-instant}") String groqModel) {
        this.groqApiKey = groqApiKey;
        this.groqModel = groqModel;
        this.restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .baseUrl("https://api.groq.com/openai/v1")
                .build();
    }

    private static final List<String> POSITIVE_WORDS = List.of(
            "excellent", "great", "good", "strong", "improved", "improvement",
            "progress", "outstanding", "helpful", "engaged", "confident",
            "successful", "creative", "well done", "amazing", "participated",
            "participation", "active", "focused", "positive", "kind", "respectful",
            "love", "wonderful", "satisfied", "fantastic", "happy", "smooth"
    );

    private static final List<String> NEGATIVE_WORDS = List.of(
            "terrible", "awful", "hate", "poor", "weak", "struggling", "struggled",
            "confused", "confusion", "difficult", "difficulty", "late", "missed",
            "incomplete", "needs improvement", "problem", "issue", "disruptive",
            "disengaged", "failed", "unprepared", "slow", "distracted", "absent",
            "unsure", "bad", "worse", "rude", "frustrating", "frustrated",
            "disappointing", "unacceptable", "broken", "not helpful", "not good",
            "very bad", "hard", "horrible"
    );

    private static final List<String> NEUTRAL_WORDS = List.of(
            "normal", "okay", "average", "completed", "submitted", "present",
            "reviewed", "attended", "class", "assignment", "topic", "review",
            "classroom", "meeting"
    );

    public Sentiment classify(String text) {
        if (text == null || text.isBlank()) {
            return Sentiment.NEUTRAL;
        }

        if (groqApiKey != null && !groqApiKey.isBlank()) {
            try {
                Sentiment groqSentiment = classifyWithGroq(text);
                if (groqSentiment != null) {
                    return groqSentiment;
                }
            } catch (Exception ignored) {
                // Fall back to deterministic rules when the external service is unavailable.
            }
        }

        return classifyRuleBased(text);
    }

    private Sentiment classifyWithGroq(String text) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", groqModel);
        payload.put("temperature", 0.1);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content",
                "Classify the sentiment of this student feedback as only POSITIVE, NEUTRAL, or NEGATIVE. Return exactly one word and nothing else. Text: " + text);

        payload.put("messages", List.of(userMessage));

        Map<String, Object> response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + groqApiKey)
                .body(payload)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.get("choices") == null) {
            return null;
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            return null;
        }

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        String content = (String) message.get("content");
        if (content == null) {
            return null;
        }

        String normalized = content.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("POSITIVE")) {
            return Sentiment.POSITIVE;
        }
        if (normalized.contains("NEGATIVE")) {
            return Sentiment.NEGATIVE;
        }
        if (normalized.contains("NEUTRAL")) {
            return Sentiment.NEUTRAL;
        }

        return null;
    }

    private Sentiment classifyRuleBased(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);
        int score = 0;

        for (String phrase : NEGATIVE_WORDS) {
            if (normalized.contains(phrase)) {
                score -= 3;
            }
        }

        for (String phrase : POSITIVE_WORDS) {
            if (normalized.contains(phrase)) {
                score += 3;
            }
        }

        for (String phrase : NEUTRAL_WORDS) {
            if (normalized.contains(phrase)) {
                score += 0;
            }
        }

        if (score > 0) {
            return Sentiment.POSITIVE;
        }
        if (score < 0) {
            return Sentiment.NEGATIVE;
        }
        return Sentiment.NEUTRAL;
    }
}
