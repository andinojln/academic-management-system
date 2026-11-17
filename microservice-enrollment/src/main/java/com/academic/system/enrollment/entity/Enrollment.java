package com.academic.system.enrollment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    // Ejecuta el method antes de que la entity se inserte en la DB
    @PrePersist
    protected void onCreate() {
        this.enrollmentDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}
