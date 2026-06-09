package com.academic.system.enrollment.service;

import com.academic.system.enrollment.dto.response.EnrollmentDTO;

import java.util.List;

public interface EnrollmentService {

    List<EnrollmentDTO> findAllByStudentId(Long studentId);

    List<EnrollmentDTO> findAllByCourseId(Long courseId);

    EnrollmentDTO save(EnrollmentDTO enrollmentDTO);

    EnrollmentDTO update(Long id, EnrollmentDTO enrollmentDTO);

    void delete(Long id);
}
