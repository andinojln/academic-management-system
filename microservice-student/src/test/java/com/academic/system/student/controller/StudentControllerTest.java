package com.academic.system.student.controller;

import com.academic.system.student.dto.StudentDTO;
import com.academic.system.student.exception.ConflictException;
import com.academic.system.student.exception.ResourceNotFoundException;
import com.academic.system.student.service.StudentService;
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

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc; // Cliente HTTP virtual para los tests

    @Autowired
    private ObjectMapper objectMapper; // Convierte Java Object -> JSON String

    @MockBean
    private StudentService studentService;

    // ===== Patrón utilizado para los Tests de Integración (Controller): Given_When_Then ===== //

    @Test
    @DisplayName("GET /api/students - Debe retornar (200 OK) y " +
            "la lista de estudiantes cuando existan registros")
    void findAll_ShouldReturn200_WhenRecordsExist() throws Exception {

        // ===== GIVEN ===== //

        StudentDTO student = new StudentDTO(
                1L,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        List<StudentDTO> expectedStudents = List.of(student);

        when(studentService.findAll()).thenReturn(expectedStudents);

        // Convierte la lista esperada a JSON usando Jackson
        String expectedJson = objectMapper.writeValueAsString(expectedStudents);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/students")
                        .accept(MediaType.APPLICATION_JSON)) // Indica que espera JSON
                .andExpect(status().isOk()) // Verifica que el status HTTP sea 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson)); // Compara el JSON completo de una sola vez

        verify(studentService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/students - Debe retornar (200 OK) " +
            "y la lista vacía cuando no existan registros")
    void findAll_ShouldReturn200_WhenNoRecordsExist() throws Exception {

        // ===== GIVEN ===== //

        List<StudentDTO> emptyList = List.of();

        when(studentService.findAll()).thenReturn(emptyList);

        String expectedJson = objectMapper.writeValueAsString(emptyList);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(studentService, times(1)).findAll();
    }

    // ====================================== //

    @Test
    @DisplayName("GET /api/students/{id} - Debe retornar (200 OK) y el estudiante cuando exista")
    void findById_ShouldReturn200_WhenStudentExists() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO student = new StudentDTO(
                studentId,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        when(studentService.findById(studentId)).thenReturn(student);

        String expectedJson = objectMapper.writeValueAsString(student);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/students/{id}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(studentService, times(1)).findById(studentId);
    }

    @Test
    @DisplayName("GET /api/students/{id} - Debe retornar (400 BAD REQUEST) cuando el ID no sea númerico")
    void findById_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        // Spring rechaza el tipo de dato antes de llamar al Service, no se mockea studentService
        mockMvc.perform(get("/api/students/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("GET /api/students/{id} - Debe retornar (404 NOT FOUND) cuando el estudiante no exista")
    void findById_ShouldReturn404_WhenStudentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        when(studentService.findById(studentId))
                .thenThrow(new ResourceNotFoundException(
                        "Estudiante no encontrado con el ID: " + studentId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(get("/api/students/{id}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Estudiante no encontrado con el ID: " + studentId));

        verify(studentService, times(1)).findById(studentId);
    }

    // ====================================== //

    @Test
    @DisplayName("POST /api/students - Debe retornar (201 CREATED) " +
            "y el estudiante creado cuando la petición sea válida")
    void save_ShouldReturn201_WhenRequestIsValid() throws Exception {

        // ===== GIVEN ===== //

        StudentDTO inputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        StudentDTO savedStudent = new StudentDTO(
                1L,
                inputStudent.name(),
                inputStudent.lastName(),
                inputStudent.email());

        when(studentService.save(any(StudentDTO.class))).thenReturn(savedStudent);

        String requestJson = objectMapper.writeValueAsString(inputStudent);
        String expectedJson = objectMapper.writeValueAsString(savedStudent);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(studentService, times(1)).save(any(StudentDTO.class));
    }

    @Test
    @DisplayName("POST /api/students - Debe retornar (400 BAD REQUEST) cuando el nombre esté en blanco")
    void save_ShouldReturn400_WhenNameIsBlank() throws Exception {

        // ===== GIVEN ===== //

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "  ",
                "Rossi",
                "s.rossi@academia.com");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("POST /api/students - Debe retornar (400 BAD REQUEST) cuando el apellido esté en blanco")
    void save_ShouldReturn400_WhenLastNameIsBlank() throws Exception {

        // ===== GIVEN ===== //

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "Santiago",
                "  ",
                "s.rossi@academia.com");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("POST /api/students - Debe retornar (400 BAD REQUEST) cuando el email esté en blanco")
    void save_ShouldReturn404_WhenEmailIsBlank() throws Exception {

        // ===== GIVEN ===== //

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "  ");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("POST /api/students - Debe retornar (400 BAD REQUEST) " +
            "cuando el formato del email sea inválido")
    void save_ShouldReturn400_WhenEmailIsInvalid() throws Exception {

        // ===== GIVEN ===== //

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "invalid-email-format");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("POST /api/students - Debe retornar (409 CONFLICT) cuando el email ya esté registrado")
    void save_ShouldReturn409_WhenEmailIsAlreadyRegistered() throws Exception {

        // ===== GIVEN ===== //

        StudentDTO inputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        when(studentService.save(any(StudentDTO.class)))
                .thenThrow(new ConflictException(
                        "El email '" + inputStudent.email() + "' ya se encuentra registrado"));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "El email '" + inputStudent.email() +
                                "' ya se encuentra registrado"));

        verify(studentService, times(1)).save(any(StudentDTO.class));
    }

    // ====================================== //

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (200 OK) y " +
            "el estudiante actualizado cuando la petición sea válida")
    void update_ShouldReturn200_WhenRequestIsValid() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO inputStudent = new StudentDTO(
                null,
                "updated__Santiago",
                "updated__Rossi",
                "updated__s.rossi@academia.com");

        StudentDTO updatedStudent = new StudentDTO(
                studentId,
                inputStudent.name(),
                inputStudent.lastName(),
                inputStudent.email());

        when(studentService.update(eq(studentId), any(StudentDTO.class)))
                .thenReturn(updatedStudent);

        String requestJson = objectMapper.writeValueAsString(inputStudent);
        String expectedJson = objectMapper.writeValueAsString(updatedStudent);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(studentService, times(1))
                .update(eq(studentId), any(StudentDTO.class));
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (400 BAD REQUEST) cuando el nombre esté en blanco")
    void update_ShouldReturn400_WhenNameIsBlank() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "  ",
                "Rossi",
                "s.rossi@academia.com");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (400 BAD REQUEST) cuando el apellido esté en blanco")
    void update_ShouldReturn400_WhenLastNameIsBlank() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "Santiago",
                "  ",
                "s.rossi@academia.com");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (400 BAD REQUEST) cuando el email esté en blanco")
    void update_ShouldReturn400_WhenEmailIsBlank() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "  ");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (400 BAD REQUEST) " +
            "cuando el formato del email sea inválido")
    void update_ShouldReturn400_WhenEmailIsInvalid() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO invalidInputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "invalid-email-format"
        );

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (400 BAD REQUEST) cuando el ID no sea numérico")
    void update_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        StudentDTO inputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (404 NOT FOUND) cuando el estudiante no exista")
    void update_ShouldReturn404_WhenStudentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO inputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        when(studentService.update(eq(studentId), any(StudentDTO.class)))
                .thenThrow(new ResourceNotFoundException(
                        "Estudiante no encontrado con el ID: " + studentId));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Estudiante no encontrado con el ID: " + studentId));

        verify(studentService, times(1))
                .update(eq(studentId), any(StudentDTO.class));
    }

    @Test
    @DisplayName("PUT /api/students/{id} - Debe retornar (409 CONFLICT) cuando el email ya esté registrado")
    void update_ShouldReturn409_WhenEmailIsAlreadyRegistered() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO inputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "v.mendez@academia.com"); // Email perteneciente a otro estudiante

        when(studentService.update(eq(studentId), any(StudentDTO.class)))
                .thenThrow(new ConflictException(
                        "El email '" + inputStudent.email() + "' ya se encuentra registrado"));

        // ===== WHEN & THEN ===== //

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputStudent)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "El email '" + inputStudent.email() +
                                "' ya se encuentra registrado"));

        verify(studentService, times(1))
                .update(eq(studentId), any(StudentDTO.class));
    }

    // ====================================== //

    @Test
    @DisplayName("DELETE /api/students/{id} - Debe retornar (204 NO CONTENT) " +
            "y eliminar el estudiante cuando exista")
    void delete_ShouldReturn204_WhenStudentExists() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).delete(studentId);
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - Debe retornar (400 BAD REQUEST) cuando el ID no sea numérico")
    void delete_ShouldReturn400_WhenIdIsNotNumeric() throws Exception {

        // ===== GIVEN ===== //

        String invalidId = "abc";

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/students/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - Debe retornar (404 NOT FOUND) cuando el estudiante no exista")
    void delete_ShouldReturn404_WhenStudentDoesNotExist() throws Exception {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        // Como delete() devuelve void, se usa doThrow()
        doThrow(new ResourceNotFoundException("Estudiante no encontrado con el ID: " + studentId))
                .when(studentService).delete(studentId);

        // ===== WHEN & THEN ===== //

        mockMvc.perform(delete("/api/students/{id}", studentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Estudiante no encontrado con el ID: " + studentId));

        verify(studentService, times(1)).delete(studentId);
    }
}
