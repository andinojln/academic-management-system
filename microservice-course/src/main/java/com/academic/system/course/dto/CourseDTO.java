package com.academic.system.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CourseDTO(

        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        String teacher
) {
}
