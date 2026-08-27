package com.classpulse.service;

import com.classpulse.dto.FeedbackRequest;
import com.classpulse.dto.FeedbackResponse;
import com.classpulse.entity.Feedback;
import com.classpulse.entity.Sentiment;
import com.classpulse.entity.Student;
import com.classpulse.exception.ResourceNotFoundException;
import com.classpulse.repository.FeedbackRepository;
import com.classpulse.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository;
    private final SentimentService sentimentService;

    public FeedbackService(FeedbackRepository feedbackRepository,
                          StudentRepository studentRepository,
                          SentimentService sentimentService) {
        this.feedbackRepository = feedbackRepository;
        this.studentRepository = studentRepository;
        this.sentimentService = sentimentService;
    }

    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        if (request == null || request.studentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (request.note() == null || request.note().isBlank()) {
            throw new IllegalArgumentException("Feedback note is required");
        }

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.studentId()));

        Sentiment sentiment = sentimentService.classify(request.note());
        Feedback feedback = new Feedback(student, request.note().trim(), LocalDateTime.now(), sentiment);
        Feedback saved = feedbackRepository.save(feedback);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedback() {
        return feedbackRepository.findAllWithStudent().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        return toResponse(feedback);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return feedbackRepository.findByStudentIdWithStudent(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FeedbackResponse updateFeedback(Long id, FeedbackRequest request) {
        if (request == null || request.studentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (request.note() == null || request.note().isBlank()) {
            throw new IllegalArgumentException("Feedback note is required");
        }

        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.studentId()));

        feedback.setStudent(student);
        feedback.setNote(request.note().trim());
        feedback.setTimestamp(LocalDateTime.now());
        feedback.setSentiment(sentimentService.classify(request.note()));

        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        feedbackRepository.delete(feedback);
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getStudent().getId(),
                feedback.getStudent().getName(),
                feedback.getNote(),
                feedback.getTimestamp(),
                feedback.getSentiment()
        );
    }
}
