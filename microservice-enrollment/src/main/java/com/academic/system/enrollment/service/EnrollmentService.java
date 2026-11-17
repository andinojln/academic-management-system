package com.academic.system.enrollment.service;

import com.academic.system.enrollment.dto.response.EnrollmentDTO;

import java.util.List;

public interface EnrollmentService {

    EnrollmentDTO findById(Long id);

    List<EnrollmentDTO> findAllByStudentId(Long studentId);

    List<EnrollmentDTO> findAllByCourseId(Long courseId);

    EnrollmentDTO save(EnrollmentDTO enrollmentRequest);

    EnrollmentDTO update(Long id, EnrollmentDTO enrollmentRequest);

    void delete(Long id);
}
