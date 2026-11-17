package com.academic.system.enrollment.controller;

import com.academic.system.enrollment.dto.client.CourseDTO;
import com.academic.system.enrollment.dto.client.StudentDTO;
import com.academic.system.enrollment.dto.response.EnrollmentDTO;
import com.academic.system.enrollment.exception.ConflictException;
import com.academic.system.enrollment.exception.ResourceNotFoundException;
import com.academic.system.enrollment.service.EnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
public class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollmentService enrollmentService;

    // ===== Patrón utilizado para los Tests de Integración (Controller): Given_When_Then ===== //

    @Test
    @DisplayName("GET /api/enrollments/{id} - Debe retornar (200 OK) y la inscripción cuando exista")
    void findById_ShouldReturn200_WhenEnrollmentExists() throws Exception {

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

        EnrollmentDTO expectedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(enrollmentService.findById(enrollmentId)).thenReturn(expectedEnrollment);

        String expectedJson = objectMapper.writeValueAsString(expectedEnrollment);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/{id}", enrollmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(enrollmentService, times(1)).findById(enrollmentId);
    }

    @Test
    @DisplayName("GET /api/enrollments/{id} - Debe retornar (400 BAD REQUEST) cuando el ID no sea númerico")
    void findById_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    @DisplayName("GET /api/enrollments/{id} - Debe retornar (404 NOT FOUND) cuando la inscripción no exista")
    void findById_ShouldReturn404_WhenEnrollmentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        when(enrollmentService.findById(enrollmentId))
                .thenThrow(new ResourceNotFoundException(
                        "Inscripción no encontrada con el ID: " + enrollmentId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/{id}", enrollmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Inscripción no encontrada con el ID: " + enrollmentId));

        verify(enrollmentService, times(1)).findById(enrollmentId);
    }

    // ====================================== //

    @Test
    @DisplayName("GET /api/enrollments/student/{studentId} - Debe retornar (200 OK) y " +
            "la lista de inscripciones cuando el estudiante y los cursos existan")
    void findAllByStudentId_ShouldReturn200_WhenStudentAndCoursesExist() throws Exception {

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

        EnrollmentDTO enrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        List<EnrollmentDTO> expectedEnrollments = List.of(enrollment);

        when(enrollmentService.findAllByStudentId(studentId)).thenReturn(expectedEnrollments);

        String expectedJson = objectMapper.writeValueAsString(expectedEnrollments);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/student/{studentId}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(enrollmentService, times(1)).findAllByStudentId(studentId);
    }

    @Test
    @DisplayName("GET /api/enrollments/student/{studentId} - Debe retornar (200 OK) "+
            "y la lista vacía cuando no existan inscripciones para el estudiante")
    void findAllByStudentId_ShouldReturn200_WhenNoEnrollmentsExist() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;
        List<EnrollmentDTO> emptyEnrollments = List.of();

        when(enrollmentService.findAllByStudentId(studentId)).thenReturn(emptyEnrollments);

        String expectedJson = objectMapper.writeValueAsString(emptyEnrollments);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/student/{studentId}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(enrollmentService, times(1)).findAllByStudentId(studentId);
    }

    @Test
    @DisplayName("GET /api/enrollments/student/{studentId} - Debe retornar " +
            "(400 BAD REQUEST) cuando el ID no sea numérico")
    void findAllByStudentId_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/student/{studentId}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    @DisplayName("GET /api/enrollments/student/{studentId} - Debe retornar " +
            "(404 NOT FOUND) cuando el estudiante no exista")
    void findAllByStudentId_ShouldReturn404_WhenStudentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        when(enrollmentService.findAllByStudentId(studentId))
                .thenThrow(new ResourceNotFoundException(
                        "Estudiante no encontrado con el ID: " + studentId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/student/{studentId}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Estudiante no encontrado con el ID: " + studentId));

        verify(enrollmentService, times(1)).findAllByStudentId(studentId);
    }

    // ====================================== //

    @Test
    @DisplayName("GET /api/enrollments/course/{courseId} - Debe retornar (200 OK) y " +
            "la lista de inscripciones cuando el curso y los estudiantes existan")
    void findAllByCourseId_ShouldReturn200_WhenCourseAndStudentsExist() throws Exception {

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

        EnrollmentDTO enrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        List<EnrollmentDTO> expectedEnrollments = List.of(enrollment);

        when(enrollmentService.findAllByCourseId(courseId)).thenReturn(expectedEnrollments);

        String expectedJson = objectMapper.writeValueAsString(expectedEnrollments);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/course/{courseId}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(enrollmentService, times(1)).findAllByCourseId(courseId);
    }

    @Test
    @DisplayName("GET /api/enrollments/course/{courseId} - Debe retornar (200 OK) " +
            "y la lista vacía cuando no existan inscripciones para el curso")
    void findAllByCourseId_ShouldReturn200_WhenNoEnrollmentsExist() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 20L;
        List<EnrollmentDTO> emptyEnrollments = List.of();

        when(enrollmentService.findAllByCourseId(courseId)).thenReturn(emptyEnrollments);

        String expectedJson = objectMapper.writeValueAsString(emptyEnrollments);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/course/{courseId}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(enrollmentService, times(1)).findAllByCourseId(courseId);
    }

    @Test
    @DisplayName("GET /api/enrollments/course/{courseId} - Debe retornar " +
            "(400 BAD REQUEST) cuando el ID no sea numérico")
    void findAllByCourseId_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/course/{courseId}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    @DisplayName("GET /api/enrollments/course/{courseId} - Debe retornar " +
            "(404 NOT FOUND) cuando el curso no exista")
    void findAllByCourseId_ShouldReturn404_WhenCourseDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 20L;

        when(enrollmentService.findAllByCourseId(courseId))
                .thenThrow(new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + courseId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/enrollments/course/{courseId}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Curso no encontrado con el ID: " + courseId));

        verify(enrollmentService, times(1)).findAllByCourseId(courseId);
    }

    // ====================================== //

    @Test
    @DisplayName("POST /api/enrollments - Debe retornar (201 CREATED) " +
            "y la inscripción creada cuando la petición sea válida")
    void save_ShouldReturn201_WhenRequestIsValid() throws Exception {

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

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        // DTO retornado por el service con el ID y datos enriquecidos
        EnrollmentDTO savedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(enrollmentService.save(any(EnrollmentDTO.class))).thenReturn(savedEnrollment);

        String requestJson = objectMapper.writeValueAsString(inputEnrollment);
        String expectedJson = objectMapper.writeValueAsString(savedEnrollment);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(enrollmentService, times(1)).save(any(EnrollmentDTO.class));
    }

    @Test
    @DisplayName("POST /api/enrollments - Debe retornar (400 BAD REQUEST) " +
            "cuando la petición no cumpla las validaciones")
    void save_ShouldReturn400_WhenRequestIsInvalid() throws Exception {

        // ===== GIVEN ===== //

        // DTO inválido (se omiten IDs obligatorios como studentId o courseId)
        EnrollmentDTO invalidInputEnrollment = new EnrollmentDTO(
                null,
                null,
                null,
                null,
                null,
                null);

        // ===== WHEN & THEN ===== //

        // @Valid interrumpe antes de invocar al servicio
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputEnrollment)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    @DisplayName("POST /api/enrollments - Debe retornar (404 NOT FOUND) cuando el estudiante no exista")
    void save_ShouldReturn404_WhenStudentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentService.save(any(EnrollmentDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Estudiante no encontrado con el ID: " + studentId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Estudiante no encontrado con el ID: " + studentId));

        verify(enrollmentService, times(1)).save(any(EnrollmentDTO.class));
    }

    @Test
    @DisplayName("POST /api/enrollments - Debe retornar (404 NOT FOUND) cuando el curso no exista")
    void save_ShouldReturn404_WhenCourseDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null
        );

        when(enrollmentService.save(any(EnrollmentDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + courseId));

        // ===== WHEN & THEN ===== //
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Curso no encontrado con el ID: " + courseId));

        verify(enrollmentService, times(1)).save(any(EnrollmentDTO.class));
    }

    @Test
    @DisplayName("POST /api/enrollments - Debe retornar (409 CONFLICT) " +
            "cuando el estudiante ya esté inscrito en el curso")
    void save_ShouldReturn409_WhenEnrollmentAlreadyExists() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentService.save(any(EnrollmentDTO.class)))
                .thenThrow(new ConflictException(
                        "El estudiante ya está inscrito en el curso"));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "El estudiante ya está inscrito en el curso"));

        verify(enrollmentService, times(1)).save(any(EnrollmentDTO.class));
    }

    // ====================================== //

    @Test
    @DisplayName("PUT /api/enrollments/{id} - Debe retornar (200 OK) y " +
            "la inscripción actualizada cuando la petición sea válida")
    void update_ShouldReturn200_WhenRequestIsValid() throws Exception {

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

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        EnrollmentDTO updatedEnrollment = new EnrollmentDTO(
                enrollmentId,
                enrollmentDate,
                studentId,
                student,
                courseId,
                course);

        when(enrollmentService.update(eq(enrollmentId), any(EnrollmentDTO.class)))
                .thenReturn(updatedEnrollment);

        String requestJson = objectMapper.writeValueAsString(inputEnrollment);
        String expectedJson = objectMapper.writeValueAsString(updatedEnrollment);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/enrollments/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(enrollmentService, times(1))
                .update(eq(enrollmentId), any(EnrollmentDTO.class));
    }

    @Test
    @DisplayName("PUT /api/enrollments/{id} - Debe retornar (400 BAD REQUEST) " +
            "cuando la petición no cumpla las validaciones")
    void update_ShouldReturn400_WhenRequestIsInvalid() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        EnrollmentDTO invalidInputEnrollment = new EnrollmentDTO(
                null,
                null,
                null,
                null,
                null,
                null);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/enrollments/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputEnrollment)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    @DisplayName("PUT /api/enrollments/{id} - Debe retornar (400 BAD REQUEST) " +
            "cuando el ID no sea numérico")
    void update_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                10L,
                null,
                20L,
                null);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/enrollments/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    @DisplayName("PUT /api/enrollments/{id} - Debe retornar (404 NOT FOUND) " +
            "cuando la inscripción no exista")
    void update_ShouldReturn404_WhenEnrollmentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentService.update(eq(enrollmentId), any(EnrollmentDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Inscripción no encontrada con el ID: " + enrollmentId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/enrollments/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Inscripción no encontrada con el ID: " + enrollmentId));

        verify(enrollmentService, times(1))
                .update(eq(enrollmentId), any(EnrollmentDTO.class));
    }

    @Test
    @DisplayName("PUT /api/enrollments/{id} - Debe retornar (404 NOT FOUND) " +
            "cuando el estudiante no exista")
    void update_ShouldReturn404_WhenStudentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentService.update(eq(enrollmentId), any(EnrollmentDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Estudiante no encontrado con el ID: " + studentId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/enrollments/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Estudiante no encontrado con el ID: " + studentId));

        verify(enrollmentService, times(1))
                .update(eq(enrollmentId), any(EnrollmentDTO.class));
    }

    @Test
    @DisplayName("PUT /api/enrollments/{id} - Debe retornar (404 NOT FOUND) cuando el curso no exista")
    void update_ShouldReturn404_WhenCourseDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentService.update(eq(enrollmentId), any(EnrollmentDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + courseId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/enrollments/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Curso no encontrado con el ID: " + courseId));

        verify(enrollmentService, times(1))
                .update(eq(enrollmentId), any(EnrollmentDTO.class));
    }

    @Test
    @DisplayName("PUT /api/enrollments/{id} - Debe retornar (409 CONFLICT) " +
            "cuando el estudiante ya esté inscrito en el curso")
    void update_ShouldReturn409_WhenEnrollmentAlreadyExists() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;
        Long studentId = 10L;
        Long courseId = 20L;

        EnrollmentDTO inputEnrollment = new EnrollmentDTO(
                null,
                null,
                studentId,
                null,
                courseId,
                null);

        when(enrollmentService.update(eq(enrollmentId), any(EnrollmentDTO.class)))
                .thenThrow(new ConflictException(
                        "El estudiante ya está inscrito en el curso"));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/enrollments/{id}", enrollmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputEnrollment)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "El estudiante ya está inscrito en el curso"));

        verify(enrollmentService, times(1))
                .update(eq(enrollmentId), any(EnrollmentDTO.class));
    }

    // ====================================== //

    @Test
    @DisplayName("DELETE /api/enrollments/{id} - Debe retornar (204 NO CONTENT) " +
            "y eliminar la inscripción cuando exista")
    void delete_ShouldReturn204_WhenEnrollmentExists() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/enrollments/{id}", enrollmentId))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).delete(enrollmentId);
    }

    @Test
    @DisplayName("DELETE /api/enrollments/{id} - Debe retornar " +
            "(400 BAD REQUEST) cuando el ID no sea numérico")
    void delete_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/enrollments/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    @DisplayName("DELETE /api/enrollments/{id} - Debe retornar " +
            "(404 NOT FOUND) cuando la inscripción no exista")
    void delete_ShouldReturn404_WhenEnrollmentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long enrollmentId = 1L;

        doThrow(new ResourceNotFoundException("Inscripción no encontrada con el ID: " + enrollmentId))
                .when(enrollmentService).delete(enrollmentId);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/enrollments/{id}", enrollmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Inscripción no encontrada con el ID: " + enrollmentId));

        verify(enrollmentService, times(1)).delete(enrollmentId);
    }
}
