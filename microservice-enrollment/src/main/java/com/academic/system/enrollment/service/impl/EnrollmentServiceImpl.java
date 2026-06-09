package com.academic.system.enrollment.service.impl;

import com.academic.system.enrollment.client.CourseClient;
import com.academic.system.enrollment.client.StudentClient;
import com.academic.system.enrollment.dto.client.CourseDTO;
import com.academic.system.enrollment.dto.client.StudentDTO;
import com.academic.system.enrollment.dto.response.EnrollmentDTO;
import com.academic.system.enrollment.entity.Enrollment;
import com.academic.system.enrollment.exception.BadRequestException;
import com.academic.system.enrollment.exception.ResourceNotFoundException;
import com.academic.system.enrollment.mapper.EnrollmentMapper;
import com.academic.system.enrollment.repository.EnrollmentRepository;
import com.academic.system.enrollment.service.EnrollmentService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final StudentClient studentClient;
    private final CourseClient courseClient;

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findAllByStudentId(Long studentId) {

        StudentDTO studentData;
        try {
            studentData = studentClient.findById(studentId);
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException("Estudiante no encontrado con el ID: " + studentId);
        }

        List<Enrollment> studentEnrollments = enrollmentRepository.findAllByStudentId(studentId);
        List<EnrollmentDTO> studentEnrollmentsDTO = enrollmentMapper.toDTOList(studentEnrollments);

        return studentEnrollmentsDTO.stream()
                .map(enrollment -> {
                    CourseDTO courseData;
                    try {
                        courseData = courseClient.findById(enrollment.courseId());
                    } catch (FeignException.NotFound error) {
                        courseData = new CourseDTO(
                                enrollment.courseId(),
                                "Curso no disponible",
                                ""
                        );
                    }
                    return new EnrollmentDTO(
                            enrollment.id(),
                            enrollment.enrollmentDate(),
                            enrollment.studentId(),
                            studentData,
                            enrollment.courseId(),
                            courseData
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findAllByCourseId(Long courseId) {

        CourseDTO courseData;
        try {
            courseData = courseClient.findById(courseId);
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException("Curso no encontrado con el ID: " + courseId);
        }

        List<Enrollment> courseEnrollments = enrollmentRepository.findAllByCourseId(courseId);
        List<EnrollmentDTO> courseEnrollmentsDTO = enrollmentMapper.toDTOList(courseEnrollments);

        return courseEnrollmentsDTO.stream()
                .map(enrollment -> {
                    StudentDTO studentData;
                    try {
                        studentData = studentClient.findById(enrollment.studentId());
                    } catch (FeignException.NotFound error) {
                        studentData = new StudentDTO(
                                enrollment.studentId(),
                                "Estudiante no disponible",
                                "",
                                ""
                        );
                    }
                    return new EnrollmentDTO(
                            enrollment.id(),
                            enrollment.enrollmentDate(),
                            enrollment.studentId(),
                            studentData,
                            enrollment.courseId(),
                            courseData
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public EnrollmentDTO save(EnrollmentDTO enrollmentDTO) {

        if (enrollmentRepository.existsByStudentIdAndCourseId(
                enrollmentDTO.studentId(),
                enrollmentDTO.courseId()
        )) {
            throw new BadRequestException("El estudiante ya está inscrito en el curso");
        }

        try {
            studentClient.findById(enrollmentDTO.studentId());
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException(
                    "Estudiante no encontrado con el ID: " + enrollmentDTO.studentId()
            );
        }
        try {
            courseClient.findById(enrollmentDTO.courseId());
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException(
                    "Curso no encontrado con el ID: " + enrollmentDTO.courseId()
            );
        }

        Enrollment enrollment = enrollmentMapper.toEntity(enrollmentDTO);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDTO(savedEnrollment);
    }

    @Override
    @Transactional
    public EnrollmentDTO update(Long id, EnrollmentDTO enrollmentDTO) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inscripción no encontrada con el ID: " + id
                ));

        // Validacion de duplicados
        boolean studentChanged = !enrollment.getStudentId().equals(enrollmentDTO.studentId());
        boolean courseChanged = !enrollment.getCourseId().equals(enrollmentDTO.courseId());
        if ((studentChanged || courseChanged) &&
                enrollmentRepository.existsByStudentIdAndCourseId(
                        enrollmentDTO.studentId(),
                        enrollmentDTO.courseId()
        )) {
            throw new BadRequestException("El estudiante ya está inscrito en el curso");
        }

        try {
            studentClient.findById(enrollmentDTO.studentId());
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException(
                    "Estudiante no encontrado con el ID: " + enrollmentDTO.studentId()
            );
        }
        try {
            courseClient.findById(enrollmentDTO.courseId());
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException(
                    "Curso no encontrado con el ID: " + enrollmentDTO.courseId()
            );
        }

        enrollment.setStudentId(enrollmentDTO.studentId());
        enrollment.setCourseId(enrollmentDTO.courseId());
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDTO(updatedEnrollment);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inscripción no encontrada con el ID: " + id);
        }

        enrollmentRepository.deleteById(id);
    }
}
