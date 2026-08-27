package com.classpulse.controller;

import com.classpulse.dto.FeedbackRequest;
import com.classpulse.dto.FeedbackResponse;
import com.classpulse.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/feedback")
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        FeedbackResponse response = feedbackService.createFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/feedback/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @PutMapping("/feedback/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(@PathVariable Long id,
                                                         @Valid @RequestBody FeedbackRequest request) {
        FeedbackResponse response = feedbackService.updateFeedback(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/feedback/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/{studentId}/feedback")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByStudentId(studentId));
    }
}
