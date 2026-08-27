package com.classpulse;

import com.classpulse.dto.FeedbackRequest;
import com.classpulse.dto.FeedbackResponse;
import com.classpulse.entity.Sentiment;
import com.classpulse.entity.Student;
import com.classpulse.exception.ResourceNotFoundException;
import com.classpulse.repository.FeedbackRepository;
import com.classpulse.repository.StudentRepository;
import com.classpulse.service.FeedbackService;
import com.classpulse.service.SentimentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({FeedbackService.class, SentimentService.class})
class FeedbackServiceTest {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SentimentService sentimentService;

    private FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackService(feedbackRepository, studentRepository, sentimentService);
    }

    @Test
    void shouldCreateFeedbackAndClassifySentiment() {
        Student student = studentRepository.save(new Student("Alice"));

        FeedbackResponse response = feedbackService.createFeedback(new FeedbackRequest(student.getId(), "Excellent work today"));

        assertEquals(student.getId(), response.studentId());
        assertEquals(Sentiment.POSITIVE, response.sentiment());
        assertNotNull(response.timestamp());
    }

    @Test
    void shouldThrowWhenStudentDoesNotExist() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> feedbackService.createFeedback(new FeedbackRequest(999L, "good work")));

        assertTrue(exception.getMessage().contains("Student not found"));
    }

    @Test
    void shouldRejectInvalidFeedbackNote() {
        Student student = studentRepository.save(new Student("Bob"));
        FeedbackRequest request = new FeedbackRequest(student.getId(), " ");

        assertThrows(Exception.class, () -> feedbackService.createFeedback(request));
    }
}
