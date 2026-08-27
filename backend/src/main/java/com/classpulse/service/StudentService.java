package com.classpulse.service;

import com.classpulse.dto.StudentRequest;
import com.classpulse.dto.StudentResponse;
import com.classpulse.entity.Student;
import com.classpulse.exception.ResourceNotFoundException;
import com.classpulse.repository.FeedbackRepository;
import com.classpulse.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FeedbackRepository feedbackRepository;

    public StudentService(StudentRepository studentRepository, FeedbackRepository feedbackRepository) {
        this.studentRepository = studentRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public StudentResponse createStudent(StudentRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Student name is required");
        }

        Student student = new Student(request.name().trim());
        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return toResponse(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        feedbackRepository.findByStudentIdWithStudent(id).forEach(feedbackRepository::delete);
        studentRepository.delete(student);
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getName());
    }
}
