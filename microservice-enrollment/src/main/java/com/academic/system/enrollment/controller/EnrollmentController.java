package com.academic.system.enrollment.controller;

import com.academic.system.enrollment.dto.response.EnrollmentDTO;
import com.academic.system.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentDTO>> findAllByStudentId(@PathVariable Long studentId) {
        List<EnrollmentDTO> studentEnrollments = enrollmentService.findAllByStudentId(studentId);
        return ResponseEntity.ok(studentEnrollments);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentDTO>> findAllByCourseId(@PathVariable Long courseId) {
        List<EnrollmentDTO> courseEnrollments = enrollmentService.findAllByCourseId(courseId);
        return ResponseEntity.ok(courseEnrollments);
    }

    @PostMapping
    public ResponseEntity<EnrollmentDTO> save(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO savedEnrollment = enrollmentService.save(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentDTO enrollmentDTO
    ) {
        EnrollmentDTO updatedEnrollment = enrollmentService.update(id, enrollmentDTO);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
