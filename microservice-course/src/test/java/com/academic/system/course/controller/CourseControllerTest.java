package com.academic.system.course.controller;

import com.academic.system.course.dto.CourseDTO;
import com.academic.system.course.exception.ResourceNotFoundException;
import com.academic.system.course.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    // ===== Patrón utilizado para los Tests de Integración (Controller): Given_When_Then ===== //

    @Test
    @DisplayName("GET /api/courses - Debe retornar (200 OK) y " +
            "la lista de cursos cuando existan registros")
    void findAll_ShouldReturn200_WhenRecordsExist() throws Exception {

        // ===== GIVEN ===== //

        CourseDTO course = new CourseDTO(
                1L,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        List<CourseDTO> expectedCourses = List.of(course);

        when(courseService.findAll()).thenReturn(expectedCourses);

        String expectedJson = objectMapper.writeValueAsString(expectedCourses);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/courses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(courseService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/courses - Debe retornar (200 OK) " +
            "y la lista vacía cuando no existan registros")
    void findAll_ShouldReturn200_WhenNoRecordsExist() throws Exception {

        // ===== GIVEN ===== //

        List<CourseDTO> emptyList = List.of();

        when(courseService.findAll()).thenReturn(emptyList);

        String expectedJson = objectMapper.writeValueAsString(emptyList);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/courses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(courseService, times(1)).findAll();
    }

    // ====================================== //

    @Test
    @DisplayName("GET /api/courses/{id} - Debe retornar (200 OK) y el curso cuando exista")
    void findById_ShouldReturn200_WhenCourseExists() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        CourseDTO course = new CourseDTO(
                courseId,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        when(courseService.findById(courseId)).thenReturn(course);

        String expectedJson = objectMapper.writeValueAsString(course);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(courseService, times(1)).findById(courseId);
    }

    @Test
    @DisplayName("GET /api/courses/{id} - Debe retornar (400 BAD REQUEST) cuando el ID no sea numérico")
    void findById_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/courses/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(courseService);
    }

    @Test
    @DisplayName("GET /api/courses/{id} - Debe retornar (404 NOT FOUND) cuando el curso no exista")
    void findById_ShouldReturn404_WhenCourseDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        when(courseService.findById(courseId))
                .thenThrow(new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + courseId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Curso no encontrado con el ID: " + courseId));

        verify(courseService, times(1)).findById(courseId);
    }

    // ====================================== //

    @Test
    @DisplayName("POST /api/courses - Debe retornar (201 CREATED) " +
            "y el curso creado cuando la petición sea válida")
    void save_ShouldReturn201_WhenRequestIsValid() throws Exception {

        // ===== GIVEN ===== //

        CourseDTO inputCourse = new CourseDTO(
                null,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        CourseDTO savedCourse = new CourseDTO(
                1L,
                inputCourse.name(),
                inputCourse.teacher());

        when(courseService.save(any(CourseDTO.class))).thenReturn(savedCourse);

        String requestJson = objectMapper.writeValueAsString(inputCourse);
        String expectedJson = objectMapper.writeValueAsString(savedCourse);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(courseService, times(1)).save(any(CourseDTO.class));
    }

    @Test
    @DisplayName("POST /api/courses - Debe retornar (201 CREATED) y el curso " +
            "creado cuando el profesor sea null (campo opcional)")
    void save_ShouldReturn201_WhenTeacherIsNull() throws Exception {

        // ===== GIVEN ===== //

        CourseDTO inputCourse = new CourseDTO(
                null,
                "Microservicios con Spring Boot",
                null);

        CourseDTO savedCourse = new CourseDTO(
                1L,
                inputCourse.name(),
                null);

        when(courseService.save(any(CourseDTO.class))).thenReturn(savedCourse);

        String requestJson = objectMapper.writeValueAsString(inputCourse);
        String expectedJson = objectMapper.writeValueAsString(savedCourse);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(courseService).save(any(CourseDTO.class));
    }

    @Test
    @DisplayName("POST /api/courses - Debe retornar (400 BAD REQUEST) cuando el nombre esté en blanco")
    void save_ShouldReturn400_WhenNameIsBlank() throws Exception {

        // ===== GIVEN ===== //

        CourseDTO invalidInputCourse = new CourseDTO(
                null,
                "  ",
                "Prof. Arnaldo");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputCourse)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(courseService);
    }

    // ====================================== //

    @Test
    @DisplayName("PUT /api/courses/{id} - Debe retornar (200 OK) y " +
            "el curso actualizado cuando la petición sea válida")
    void update_ShouldReturn200_WhenRequestIsValid() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        CourseDTO inputCourse = new CourseDTO(
                null,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        CourseDTO updatedCourse = new CourseDTO(
                courseId,
                inputCourse.name(),
                inputCourse.teacher());

        when(courseService.update(eq(courseId), any(CourseDTO.class)))
                .thenReturn(updatedCourse);

        String requestJson = objectMapper.writeValueAsString(inputCourse);
        String expectedJson = objectMapper.writeValueAsString(updatedCourse);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(courseService, times(1))
                .update(eq(courseId), any(CourseDTO.class));
    }

    @Test
    @DisplayName("PUT /api/courses/{id} - Debe retornar (400 BAD REQUEST) cuando el nombre esté en blanco")
    void update_ShouldReturn400_WhenNameIsBlank() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        CourseDTO invalidInputCourse = new CourseDTO(
                null,
                "  ",
                "Prof. Arnaldo");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputCourse)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(courseService);
    }

    @Test
    @DisplayName("PUT /api/courses/{id} - Debe retornar (400 BAD REQUEST) cuando el ID no sea numérico")
    void update_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        CourseDTO inputCourse = new CourseDTO(
                null,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/courses/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCourse)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(courseService);
    }

    @Test
    @DisplayName("PUT /api/courses/{id} - Debe retornar (404 NOT FOUND) cuando el curso no exista")
    void update_ShouldReturn404_WhenCourseDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        CourseDTO inputCourse = new CourseDTO(
                null,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        when(courseService.update(eq(courseId), any(CourseDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + courseId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputCourse)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Curso no encontrado con el ID: " + courseId));

        verify(courseService, times(1))
                .update(eq(courseId), any(CourseDTO.class));
    }

    // ====================================== //

    @Test
    @DisplayName("DELETE /api/courses/{id} - Debe retornar (204 NO CONTENT) " +
            "y eliminar el curso cuando exista")
    void delete_ShouldReturn204_WhenCourseExists() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/courses/{id}", courseId))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).delete(courseId);
    }

    @Test
    @DisplayName("DELETE /api/courses/{id} - Debe retornar (400 BAD REQUEST) cuando el ID no sea numérico")
    void delete_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/courses/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(courseService);
    }

    @Test
    @DisplayName("DELETE /api/courses/{id} - Debe retornar (404 NOT FOUND) cuando el curso no exista")
    void delete_ShouldReturn404_WhenCourseDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        doThrow(new ResourceNotFoundException("Curso no encontrado con el ID: " + courseId))
                .when(courseService).delete(courseId);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/courses/{id}", courseId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Curso no encontrado con el ID: " + courseId));

        verify(courseService, times(1)).delete(courseId);
    }
}
