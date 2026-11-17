package com.academic.system.enrollment.service.impl;

import com.academic.system.enrollment.client.CourseClient;
import com.academic.system.enrollment.client.StudentClient;
import com.academic.system.enrollment.dto.client.CourseDTO;
import com.academic.system.enrollment.dto.client.StudentDTO;
import com.academic.system.enrollment.dto.response.EnrollmentDTO;
import com.academic.system.enrollment.entity.Enrollment;
import com.academic.system.enrollment.exception.ConflictException;
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
    public EnrollmentDTO findById(Long id) {

        Enrollment enrollment = findEnrollmentOrThrow(id);
        StudentDTO student = fetchStudentSafely(enrollment.getStudentId());
        CourseDTO course = fetchCourseSafely(enrollment.getCourseId());

        return buildEnrollmentDTO(
                enrollment,
                student,
                course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findAllByStudentId(Long studentId) {

        StudentDTO student = validateAndFetchStudent(studentId);

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(studentId);

        return enrollments.stream()
                .map(enrollment ->
                        mapToEnrollmentDTOWithStudent(enrollment, student)
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> findAllByCourseId(Long courseId) {

        CourseDTO course = validateAndFetchCourse(courseId);

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);

        return enrollments.stream()
                .map(enrollment ->
                        mapToEnrollmentDTOWithCourse(enrollment, course))
                .toList();
    }

    @Override
    @Transactional
    public EnrollmentDTO save(EnrollmentDTO enrollmentRequest) {

        validateEnrollment(enrollmentRequest.studentId(), enrollmentRequest.courseId());

        StudentDTO student = validateAndFetchStudent(enrollmentRequest.studentId());
        CourseDTO course = validateAndFetchCourse(enrollmentRequest.courseId());

        Enrollment enrollment = enrollmentMapper.toEntity(enrollmentRequest);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return buildEnrollmentDTO(savedEnrollment, student, course);
    }

    @Override
    @Transactional
    public EnrollmentDTO update(Long id, EnrollmentDTO enrollmentRequest) {

        Enrollment enrollment = findEnrollmentOrThrow(id);

        validateDuplicateOnUpdate(enrollment, enrollmentRequest);

        StudentDTO student = validateAndFetchStudent(enrollmentRequest.studentId());
        CourseDTO course = validateAndFetchCourse(enrollmentRequest.courseId());

        enrollment.setStudentId(enrollmentRequest.studentId());
        enrollment.setCourseId(enrollmentRequest.courseId());

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return buildEnrollmentDTO(updatedEnrollment, student, course);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        findEnrollmentOrThrow(id);
        enrollmentRepository.deleteById(id);
    }

    // ===== MÉTODOS AUXILIARES PRIVADOS (CLEAN CODE & SRP) ===== //

    private Enrollment findEnrollmentOrThrow(Long id) {

        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inscripción no encontrada con el ID: " + id));
    }

    private StudentDTO fetchStudentSafely(Long studentId) {

        try {
            return studentClient.findById(studentId);
        } catch (FeignException.NotFound e) {
            return new StudentDTO(
                    studentId,
                    "Estudiante no disponible",
                    "",
                    ""
            );
        }
    }

    private CourseDTO fetchCourseSafely(Long courseId) {

        try {
            return courseClient.findById(courseId);
        } catch (FeignException.NotFound e) {
            return new CourseDTO(
                    courseId,
                    "Curso no disponible",
                    ""
            );
        }
    }

    private EnrollmentDTO buildEnrollmentDTO(
            Enrollment enrollment,
            StudentDTO student,
            CourseDTO course) {

        return new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStudentId(),
                student,
                enrollment.getCourseId(),
                course
        );
    }

    private StudentDTO validateAndFetchStudent(Long studentId) {

        try {
            return studentClient.findById(studentId);
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException("Estudiante no encontrado con el ID: " + studentId);
        }
    }

    private CourseDTO validateAndFetchCourse(Long courseId) {

        try {
            return courseClient.findById(courseId);
        } catch (FeignException.NotFound error) {
            throw new ResourceNotFoundException("Curso no encontrado con el ID: " + courseId);
        }
    }

    private void validateEnrollment(Long studentId, Long courseId) {

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ConflictException("El estudiante ya está inscrito en el curso");
        }
    }

    private void validateDuplicateOnUpdate(Enrollment enrollment, EnrollmentDTO enrollmentRequest) {

        boolean studentChanged = !enrollment.getStudentId().equals(enrollmentRequest.studentId());
        boolean courseChanged = !enrollment.getCourseId().equals(enrollmentRequest.courseId());

        if ((studentChanged || courseChanged) &&
                enrollmentRepository.existsByStudentIdAndCourseId(
                        enrollmentRequest.studentId(),
                        enrollmentRequest.courseId()
                )) {
            throw new ConflictException("Ya existe una inscripción para el estudiante en este curso");
        }
    }

    private EnrollmentDTO mapToEnrollmentDTOWithStudent(Enrollment enrollment, StudentDTO student) {

        CourseDTO course = fetchCourseSafely(enrollment.getCourseId());

        return new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStudentId(),
                student,
                enrollment.getCourseId(),
                course
        );
    }

    private EnrollmentDTO mapToEnrollmentDTOWithCourse(Enrollment enrollment, CourseDTO course) {

        StudentDTO student = fetchStudentSafely(enrollment.getStudentId());

        return new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getEnrollmentDate(),
                enrollment.getStudentId(),
                student,
                enrollment.getCourseId(),
                course
        );
    }
}
