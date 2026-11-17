package com.academic.system.student.controller;

import com.academic.system.student.dto.StudentDTO;
import com.academic.system.student.exception.ErrorDTO;
import com.academic.system.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(
        name = "Estudiantes",
        description = "Endpoints para la gestión de los estudiantes del Sistema Académico."
)
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los estudiantes",
            description = "Devuelve el listado de todos los estudiantes registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiantes recuperados exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = StudentDTO.class)
                            )
                    )
            )
    })
    public ResponseEntity<List<StudentDTO>> findAll() {

        List<StudentDTO> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener datos de un estudiante",
            description = "Devuelve los datos de un estudiante específico mediante su ID único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiante recuperado exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador del estudiante inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<StudentDTO> findById(
            @Parameter(description = "ID único del estudiante a consultar.", example = "1")
            @PathVariable Long id
    ) {
        StudentDTO student = studentService.findById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo estudiante",
            description = "Registra un nuevo estudiante en el sistema tras validar " +
                    "sus datos obligatorios y la unicidad del email."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Estudiante registrado exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del estudiante inválidos o incompletos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya se encuentra registrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<StudentDTO> save(@Valid @RequestBody StudentDTO student) {

        StudentDTO savedStudent = studentService.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un estudiante",
            description = "Actualiza la información de un estudiante existente " +
                    "tras validar sus datos y la unicidad del email."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiante actualizado exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido o datos del estudiante incompletos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya se encuentra registrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<StudentDTO> update(
            @Parameter(description = "ID único del estudiante a actualizar.", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO student
    ) {
        StudentDTO updatedStudent = studentService.update(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un estudiante",
            description = "Elimina un estudiante del sistema mediante su ID único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Estudiante eliminado exitosamente.",
                    content = @Content // @Content vacío porque no hay body (204)
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador del estudiante inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único del estudiante a eliminar.", example = "1")
            @PathVariable Long id
    ) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
