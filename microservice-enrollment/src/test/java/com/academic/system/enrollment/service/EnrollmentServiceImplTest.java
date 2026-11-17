package com.academic.system.enrollment.service;

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
import com.academic.system.enrollment.service.impl.EnrollmentServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @Mock
    private StudentClient studentClient;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    // ===== Patrón utilizado para los Tests Unitarios (Service): Given_When_Then ===== //

    @Test
    @DisplayName("Debe retornar la inscripción con datos enriquecidos " +
            "cuando el estudiante y el curso existan")
    void findById_ShouldReturnEnrollment_WhenStudentAndCourseExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(studentClient.findById(studentId)).thenReturn(student);
        when(courseClient.findById(courseId)).thenReturn(course);

        // ===== WHEN ===== //

        EnrollmentDTO enrollmentResult = enrollmentService.findById(enrollmentId);

        // ===== THEN ===== //

        assertThat(enrollmentResult)
                .isNotNull()
                .isEqualTo(expectedEnrollment);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(studentClient, times(1)).findById(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe retornar la inscripción con datos de fallback " +
            "cuando el estudiante no exista pero el curso sí")
    void findById_ShouldReturnEnrollmentWithFallback_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        StudentDTO fallbackStudent = new StudentDTO(
                studentId,
                "Estudiante no disponible",
                "",
                "");

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                fallbackStudent,
                courseId,
                course);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(studentClient.findById(studentId)).thenThrow(notFoundException);
        when(courseClient.findById(courseId)).thenReturn(course);

        // ===== WHEN ===== //

        EnrollmentDTO enrollmentResult = enrollmentService.findById(enrollmentId);

        // ===== THEN ===== //

        assertThat(enrollmentResult)
                .isNotNull()
                .isEqualTo(expectedEnrollment);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(studentClient, times(1)).findById(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe retornar la inscripción con datos de fallback " +
            "cuando el curso no exista pero el estudiante sí")
    void findById_ShouldReturnEnrollmentWithFallback_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO fallbackCourse = new CourseDTO(
                courseId,
                "Curso no disponible",
                "");

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                fallbackCourse);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(studentClient.findById(studentId)).thenReturn(student);
        when(courseClient.findById(courseId)).thenThrow(notFoundException);

        // ===== WHEN ===== //

        EnrollmentDTO enrollmentResult = enrollmentService.findById(enrollmentId);

        // ===== THEN ===== //

        assertThat(enrollmentResult)
                .isNotNull()
                .isEqualTo(expectedEnrollment);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(studentClient, times(1)).findById(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando la inscripción no exista")
    void findById_ShouldThrowResourceNotFoundException_WhenEnrollmentDoesNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.findById(enrollmentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inscripción no encontrada con el ID: " + enrollmentId);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verifyNoInteractions(studentClient, courseClient, enrollmentMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar la lista de inscripciones " +
            "cuando el estudiante y los cursos existan")
    void findAllByStudentId_ShouldReturnEnrollmentsList_WhenStudentAndCoursesExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        List<Enrollment> enrollments = List.of(enrollment);

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(studentClient.findById(studentId)).thenReturn(student);
        when(enrollmentRepository.findAllByStudentId(studentId)).thenReturn(enrollments);
        when(courseClient.findById(courseId)).thenReturn(course);

        // ===== WHEN ===== //

        List<EnrollmentDTO> enrollmentsResult = enrollmentService.findAllByStudentId(studentId);

        // ===== THEN ===== //

        assertThat(enrollmentsResult)
                .isNotNull()
                .hasSize(1)
                .containsExactly(expectedEnrollment);

        verify(studentClient, times(1)).findById(studentId);
        verify(enrollmentRepository, times(1)).findAllByStudentId(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe retornar la lista vacía cuando el estudiante " +
            "exista pero no tenga inscripciones")
    void findAllByStudentId_ShouldReturnEmptyList_WhenStudentExistsButHasNoEnrollments() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        when(studentClient.findById(studentId)).thenReturn(student);
        when(enrollmentRepository.findAllByStudentId(studentId)).thenReturn(List.of());

        // ===== WHEN ===== //

        List<EnrollmentDTO> enrollmentsResult = enrollmentService.findAllByStudentId(studentId);

        // ===== THEN ===== //

        assertThat(enrollmentsResult)
                .isNotNull()
                .isEmpty();

        verify(studentClient, times(1)).findById(studentId);
        verify(enrollmentRepository, times(1)).findAllByStudentId(studentId);
        verifyNoInteractions(courseClient, enrollmentMapper);
    }

    @Test
    @DisplayName("Debe retornar la lista de inscripciones con " +
            "datos de fallback cuando los cursos no existan")
    void findAllByStudentId_ShouldReturnEnrollmentsListWithFallback_WhenCoursesDoNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;
        FeignException.NotFound feignNotFoundException = mock(FeignException.NotFound.class);

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO fallbackCourse = new CourseDTO(
                courseId,
                "Curso no disponible",
                "");

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        List<Enrollment> enrollments = List.of(enrollment);

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                fallbackCourse);

        when(studentClient.findById(studentId)).thenReturn(student);
        when(enrollmentRepository.findAllByStudentId(studentId)).thenReturn(enrollments);
        when(courseClient.findById(courseId)).thenThrow(feignNotFoundException);

        // ===== WHEN ===== //

        List<EnrollmentDTO> enrollmentsResult = enrollmentService.findAllByStudentId(studentId);

        // ===== THEN ===== //

        assertThat(enrollmentsResult)
                .isNotNull()
                .hasSize(1)
                .containsExactly(expectedEnrollment);

        verify(studentClient, times(1)).findById(studentId);
        verify(enrollmentRepository, times(1)).findAllByStudentId(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el estudiante no exista")
    void findAllByStudentId_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long studentId = 1L;
        FeignException.NotFound feignNotFoundException = mock(FeignException.NotFound.class);

        when(studentClient.findById(studentId)).thenThrow(feignNotFoundException);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.findAllByStudentId(studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Estudiante no encontrado con el ID: " + studentId);

        verify(studentClient, times(1)).findById(studentId);
        verifyNoInteractions(enrollmentRepository, courseClient, enrollmentMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar la lista de inscripciones " +
            "cuando el curso y los estudiantes existan")
    void findAllByCourseId_ShouldReturnEnrollmentsList_WhenCourseAndStudentsExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        List<Enrollment> enrollments = List.of(enrollment);

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(courseClient.findById(courseId)).thenReturn(course);
        when(enrollmentRepository.findAllByCourseId(courseId)).thenReturn(enrollments);
        when(studentClient.findById(studentId)).thenReturn(student);

        // ===== WHEN ===== //

        List<EnrollmentDTO> enrollmentsResult = enrollmentService.findAllByCourseId(courseId);

        // ===== THEN ===== //

        assertThat(enrollmentsResult)
                .isNotNull()
                .hasSize(1)
                .containsExactly(expectedEnrollment);

        verify(courseClient, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).findAllByCourseId(courseId);
        verify(studentClient, times(1)).findById(studentId);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe retornar la lista vacía cuando el curso " +
            "exista pero no tenga inscripciones")
    void findAllByCourseId_ShouldReturnEmptyList_WhenCourseExistsButNoEnrollments() {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        when(courseClient.findById(courseId)).thenReturn(course);
        when(enrollmentRepository.findAllByCourseId(courseId)).thenReturn(List.of());

        // ===== WHEN ===== //

        List<EnrollmentDTO> enrollmentsResult = enrollmentService.findAllByCourseId(courseId);

        // ===== THEN ===== //

        assertThat(enrollmentsResult)
                .isNotNull()
                .isEmpty();

        verify(courseClient, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).findAllByCourseId(courseId);
        verifyNoInteractions(studentClient, enrollmentMapper);
    }

    @Test
    @DisplayName("Debe retornar la lista de inscripciones con " +
            "datos de fallback cuando los estudiantes no existan")
    void findAllByCourseId_ShouldReturnEnrollmentsListWithFallback_WhenStudentsDoNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;
        FeignException.NotFound feignNotFoundException = mock(FeignException.NotFound.class);

        StudentDTO fallbackStudent = new StudentDTO(
                studentId,
                "Estudiante no disponible",
                "",
                "");

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);

        List<Enrollment> enrollments = List.of(enrollment);

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                fallbackStudent,
                courseId,
                course);

        when(courseClient.findById(courseId)).thenReturn(course);
        when(enrollmentRepository.findAllByCourseId(courseId)).thenReturn(enrollments);
        when(studentClient.findById(studentId)).thenThrow(feignNotFoundException);

        // ===== WHEN ===== //

        List<EnrollmentDTO> enrollmentsResult = enrollmentService.findAllByCourseId(courseId);

        // ===== THEN ===== //

        assertThat(enrollmentsResult)
                .isNotNull()
                .hasSize(1)
                .containsExactly(expectedEnrollment);

        verify(courseClient, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).findAllByCourseId(courseId);
        verify(studentClient, times(1)).findById(studentId);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el curso no exista")
    void findAllByCourseId_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long courseId = 1L;
        FeignException.NotFound feignNotFoundException = mock(FeignException.NotFound.class);

        when(courseClient.findById(courseId)).thenThrow(feignNotFoundException);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.findAllByCourseId(courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado con el ID: " + courseId);

        verify(courseClient, times(1)).findById(courseId);
        verifyNoInteractions(enrollmentRepository, studentClient, enrollmentMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe guardar y retornar la inscripción " +
            "cuando el estudiante y el curso existan")
    void save_ShouldReturnEnrollment_WhenStudentAndCourseExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        Enrollment enrollmentToSave = new Enrollment();
        enrollmentToSave.setStudentId(enrollmentRequest.studentId());
        enrollmentToSave.setCourseId(enrollmentRequest.courseId());

        // Entidad simulada ya guardada en la DB con ID y fecha de inscripción
        Enrollment savedEnrollment = new Enrollment();
        savedEnrollment.setId(enrollmentId);
        savedEnrollment.setEnrollmentDate(enrollmentDate);
        savedEnrollment.setStudentId(studentId);
        savedEnrollment.setCourseId(courseId);

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(false);

        when(studentClient.findById(studentId)).thenReturn(student);
        when(courseClient.findById(courseId)).thenReturn(course);
        when(enrollmentMapper.toEntity(enrollmentRequest)).thenReturn(enrollmentToSave);
        when(enrollmentRepository.save(enrollmentToSave)).thenReturn(savedEnrollment);

        // ===== WHEN ===== //

        EnrollmentDTO enrollmentResult = enrollmentService.save(enrollmentRequest);

        // ===== THEN ===== //

        assertThat(enrollmentResult)
                .isNotNull()
                .isEqualTo(expectedEnrollment);

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(studentId, courseId);

        verify(studentClient, times(1)).findById(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verify(enrollmentMapper, times(1)).toEntity(enrollmentRequest);
        verify(enrollmentRepository, times(1)).save(enrollmentToSave);
        verifyNoMoreInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ConflictException) cuando " +
            "el estudiante ya esté inscrito en el curso")
    void save_ShouldThrowConflictException_WhenEnrollmentAlreadyExists() {

        // ===== GIVEN ===== //

        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(true);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.save(enrollmentRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El estudiante ya está inscrito en el curso");

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(studentId, courseId);

        verifyNoInteractions(studentClient, courseClient, enrollmentMapper);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el estudiante no exista")
    void save_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long studentId = 10L;
        Long courseId = 20L;
        FeignException.NotFound feignNotFoundException = mock(FeignException.NotFound.class);

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(false);

        when(studentClient.findById(studentId)).thenThrow(feignNotFoundException);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.save(enrollmentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Estudiante no encontrado con el ID: " + studentId);

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(studentId, courseId);

        verify(studentClient, times(1)).findById(studentId);
        verifyNoInteractions(courseClient, enrollmentMapper);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el curso no exista")
    void save_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long studentId = 10L;
        Long courseId = 20L;
        FeignException.NotFound feignNotFoundException = mock(FeignException.NotFound.class);

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(false);

        when(studentClient.findById(studentId)).thenReturn(student);
        when(courseClient.findById(courseId)).thenThrow(feignNotFoundException);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.save(enrollmentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado con el ID: " + courseId);

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(studentId, courseId);

        verify(studentClient, times(1)).findById(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verifyNoInteractions(enrollmentMapper);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    // ====================================== //

    @Test
    @DisplayName("Debe actualizar y retornar la inscripción " +
            "cuando el estudiante y el curso existan")
    void update_ShouldReturnEnrollment_WhenStudentAndCourseExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long newStudentId = 11L;
        Long courseId = 20L;
        Long newCourseId = 21L;

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(enrollmentId);
        existingEnrollment.setEnrollmentDate(enrollmentDate);
        existingEnrollment.setStudentId(studentId);
        existingEnrollment.setCourseId(courseId);

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                newStudentId,
                null,
                newCourseId,
                null);

        StudentDTO student = new StudentDTO(
                newStudentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO course = new CourseDTO(
                newCourseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                newStudentId,
                student,
                newCourseId,
                course);

        when(enrollmentRepository.findById(enrollmentId))
                .thenReturn(Optional.of(existingEnrollment));

        when(enrollmentRepository.existsByStudentIdAndCourseId(newStudentId, newCourseId))
                .thenReturn(false);

        when(studentClient.findById(newStudentId)).thenReturn(student);
        when(courseClient.findById(newCourseId)).thenReturn(course);
        when(enrollmentRepository.save(existingEnrollment)).thenReturn(existingEnrollment);

        // ===== WHEN ===== //

        EnrollmentDTO enrollmentResult = enrollmentService.update(enrollmentId, enrollmentRequest);

        // ===== THEN ===== //

        assertThat(enrollmentResult)
                .isNotNull()
                .isEqualTo(expectedEnrollment);

        // Verifica que los datos existentes fueron actualizados con los nuevos valores
        assertThat(existingEnrollment.getStudentId()).isEqualTo(newStudentId);
        assertThat(existingEnrollment.getCourseId()).isEqualTo(newCourseId);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(newStudentId, newCourseId);

        verify(studentClient, times(1)).findById(newStudentId);
        verify(courseClient, times(1)).findById(newCourseId);
        verify(enrollmentRepository, times(1)).save(existingEnrollment);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe actualizar y retornar la inscripción cuando " +
            "el estudiante y el curso no sean modificados")
    void update_ShouldReturnEnrollment_WhenStudentAndCourseAreNotModified() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(enrollmentId);
        existingEnrollment.setEnrollmentDate(enrollmentDate);
        existingEnrollment.setStudentId(studentId);
        existingEnrollment.setCourseId(courseId);

        // Mismos IDs de estudiante y curso
        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(enrollmentRepository.findById(enrollmentId))
                .thenReturn(Optional.of(existingEnrollment));

        when(studentClient.findById(studentId)).thenReturn(student);
        when(courseClient.findById(courseId)).thenReturn(course);
        when(enrollmentRepository.save(existingEnrollment)).thenReturn(existingEnrollment);

        // ===== WHEN ===== //

        EnrollmentDTO enrollmentResult = enrollmentService.update(enrollmentId, enrollmentRequest);

        // ===== THEN ===== //

        assertThat(enrollmentResult)
                .isNotNull()
                .isEqualTo(expectedEnrollment);

        // Confirma que los datos se mantuvieron intactos
        assertThat(existingEnrollment.getStudentId()).isEqualTo(studentId);
        assertThat(existingEnrollment.getCourseId()).isEqualTo(courseId);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(enrollmentRepository, never()).existsByStudentIdAndCourseId(anyLong(), anyLong());
        verify(studentClient, times(1)).findById(studentId);
        verify(courseClient, times(1)).findById(courseId);
        verify(enrollmentRepository, times(1)).save(existingEnrollment);
        verifyNoInteractions(enrollmentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ConflictException) cuando ya exista " +
            "una inscripción para el estudiante en el curso")
    void update_ShouldThrowConflictException_WhenEnrollmentAlreadyExists() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long newStudentId = 11L;
        Long courseId = 20L;
        Long newCourseId = 21L;

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(enrollmentId);
        existingEnrollment.setEnrollmentDate(enrollmentDate);
        existingEnrollment.setStudentId(studentId);
        existingEnrollment.setCourseId(courseId);

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                newStudentId,
                null,
                newCourseId,
                null);

        when(enrollmentRepository.findById(enrollmentId))
                .thenReturn(Optional.of(existingEnrollment));

        when(enrollmentRepository.existsByStudentIdAndCourseId(newStudentId, newCourseId))
                .thenReturn(true);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.update(enrollmentId, enrollmentRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Ya existe una inscripción para el estudiante en este curso");

        verify(enrollmentRepository, times(1)).findById(enrollmentId);

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(newStudentId, newCourseId);

        verifyNoInteractions(studentClient, courseClient, enrollmentMapper);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando la inscripción no exista")
    void update_ShouldThrowResourceNotFoundException_WhenEnrollmentDoesNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                10L,
                null,
                20L,
                null);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.update(enrollmentId, enrollmentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inscripción no encontrada con el ID: " + enrollmentId);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(enrollmentRepository, never()).existsByStudentIdAndCourseId(anyLong(), anyLong());
        verifyNoInteractions(studentClient, courseClient, enrollmentMapper);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el estudiante no exista")
    void update_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long newStudentId = 11L;
        Long courseId = 20L;
        FeignException.NotFound feignNotFound = mock(FeignException.NotFound.class);

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(enrollmentId);
        existingEnrollment.setEnrollmentDate(enrollmentDate);
        existingEnrollment.setStudentId(studentId);
        existingEnrollment.setCourseId(courseId);

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                newStudentId,
                null,
                courseId,
                null);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(existingEnrollment));

        when(enrollmentRepository.existsByStudentIdAndCourseId(newStudentId, courseId))
                .thenReturn(false);

        when(studentClient.findById(newStudentId)).thenThrow(feignNotFound);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.update(enrollmentId, enrollmentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Estudiante no encontrado con el ID: " + newStudentId);

        verify(enrollmentRepository, times(1))
                .findById(enrollmentId);

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(newStudentId, courseId);

        verify(studentClient, times(1)).findById(newStudentId);
        verifyNoInteractions(courseClient, enrollmentMapper);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el curso no exista")
    void update_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        LocalDateTime enrollmentDate = LocalDateTime.of(
                2026, 3, 30, 10, 15, 30);
        Long studentId = 10L;
        Long courseId = 20L;
        Long newCourseId = 21L;
        FeignException.NotFound feignNotFound = mock(FeignException.NotFound.class);

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(enrollmentId);
        existingEnrollment.setEnrollmentDate(enrollmentDate);
        existingEnrollment.setStudentId(studentId);
        existingEnrollment.setCourseId(courseId);

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        EnrollmentDTO enrollmentRequest = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                newCourseId,
                null);

        when(enrollmentRepository.findById(enrollmentId))
                .thenReturn(Optional.of(existingEnrollment));

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, newCourseId))
                .thenReturn(false);

        when(studentClient.findById(studentId)).thenReturn(student);
        when(courseClient.findById(newCourseId)).thenThrow(feignNotFound);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.update(enrollmentId, enrollmentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado con el ID: " + newCourseId);

        verify(enrollmentRepository, times(1))
                .findById(enrollmentId);

        verify(enrollmentRepository, times(1))
                .existsByStudentIdAndCourseId(studentId, newCourseId);

        verify(studentClient, times(1)).findById(studentId);
        verify(courseClient, times(1)).findById(newCourseId);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
        verifyNoInteractions(enrollmentMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe eliminar la inscripción cuando exista")
    void delete_ShouldDeleteEnrollment_WhenEnrollmentExists() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setId(enrollmentId);
        existingEnrollment.setStudentId(10L);
        existingEnrollment.setCourseId(100L);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(existingEnrollment));

        // ===== WHEN & THEN ===== //

        assertThatNoException()
                .isThrownBy(() -> enrollmentService.delete(enrollmentId));

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(enrollmentRepository, times(1)).deleteById(enrollmentId);
        verifyNoInteractions(studentClient, courseClient, enrollmentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando la inscripción no exista")
    void delete_ShouldThrowResourceNotFoundException_WhenEnrollmentDoesNotExist() {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentService.delete(enrollmentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Inscripción no encontrada con el ID: " + enrollmentId);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(enrollmentRepository, never()).deleteById(anyLong());
        verifyNoInteractions(studentClient, courseClient, enrollmentMapper);
    }
}
