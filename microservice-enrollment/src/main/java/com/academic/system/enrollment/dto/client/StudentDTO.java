package com.academic.system.enrollment.dto.client;

public record StudentDTO(
        Long id,
        String name,
        String lastName,
        String email
) {
}
