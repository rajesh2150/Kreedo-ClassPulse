package com.classpulse.repository;

import com.classpulse.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
            select f from Feedback f
            join fetch f.student s
            order by f.timestamp desc
            """)
    List<Feedback> findAllWithStudent();

    @Query("""
            select f from Feedback f
            join fetch f.student s
            where s.id = :studentId
            order by f.timestamp desc
            """)
    List<Feedback> findByStudentIdWithStudent(@Param("studentId") Long studentId);
}
