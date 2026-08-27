package com.classpulse;

import com.classpulse.entity.Sentiment;
import com.classpulse.service.SentimentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentimentServiceTest {

    private final SentimentService sentimentService = new SentimentService();

    @Test
    void shouldClassifyPositiveText() {
        assertEquals(Sentiment.POSITIVE, sentimentService.classify("Excellent work today"));
    }

    @Test
    void shouldClassifyNegativeText() {
        assertEquals(Sentiment.NEGATIVE, sentimentService.classify("Student is struggling with the topic"));
    }

    @Test
    void shouldClassifyNeutralText() {
        assertEquals(Sentiment.NEUTRAL, sentimentService.classify("Student attended today's class"));
    }
}
