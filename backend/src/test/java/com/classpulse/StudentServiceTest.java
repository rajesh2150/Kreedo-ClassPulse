package com.classpulse;

import com.classpulse.dto.StudentRequest;
import com.classpulse.dto.StudentResponse;
import com.classpulse.entity.Student;
import com.classpulse.exception.ResourceNotFoundException;
import com.classpulse.repository.FeedbackRepository;
import com.classpulse.repository.StudentRepository;
import com.classpulse.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(StudentService.class)
class StudentServiceTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, feedbackRepository);
    }

    @Test
    void shouldCreateStudent() {
        StudentResponse response = studentService.createStudent(new StudentRequest("John Smith"));

        assertNotNull(response.id());
        assertEquals("John Smith", response.name());
    }

    @Test
    void shouldThrowWhenStudentDoesNotExist() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> studentService.getStudentById(999L));

        assertTrue(exception.getMessage().contains("Student not found"));
    }

    @Test
    void shouldRejectInvalidStudentName() {
        StudentRequest request = new StudentRequest(" ");
        assertThrows(Exception.class, () -> studentService.createStudent(request));
    }
}
