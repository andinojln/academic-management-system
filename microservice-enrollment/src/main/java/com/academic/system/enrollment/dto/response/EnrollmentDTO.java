package com.academic.system.enrollment.dto.response;

import com.academic.system.enrollment.dto.client.CourseDTO;
import com.academic.system.enrollment.dto.client.StudentDTO;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EnrollmentDTO(

        Long id,

        LocalDateTime enrollmentDate,

        @NotNull(message = "El estudiante es obligatorio")
        Long studentId,

        StudentDTO student,

        @NotNull(message = "El curso es obligatorio")
        Long courseId,

        CourseDTO course
) {
}
