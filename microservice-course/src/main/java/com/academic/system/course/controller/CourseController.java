package com.academic.system.course.controller;

import com.academic.system.course.dto.CourseDTO;
import com.academic.system.course.exception.ErrorDTO;
import com.academic.system.course.service.CourseService;
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
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(
        name = "Cursos",
        description = "Endpoints para la gestión de los cursos del Sistema Académico."
)
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los cursos",
            description = "Devuelve el listado de todos los cursos registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cursos recuperados exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = CourseDTO.class)
                            )
                    )
            )
    })
    public ResponseEntity<List<CourseDTO>> findAll() {

        List<CourseDTO> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener datos de un curso",
            description = "Devuelve los datos de un curso específico mediante su ID único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Curso recuperado exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador del curso inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<CourseDTO> findById(
            @Parameter(description = "ID único del curso a consultar.", example = "1")
            @PathVariable Long id
    ) {
        CourseDTO course = courseService.findById(id);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo curso",
            description = "Registra un nuevo curso en el sistema tras validar sus datos obligatorios."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Curso registrado exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del curso incompletos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<CourseDTO> save(@Valid @RequestBody CourseDTO course) {

        CourseDTO savedCourse = courseService.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un curso",
            description = "Actualiza la información de un curso existente tras validar sus datos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Curso actualizado exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido o datos del curso incompletos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<CourseDTO> update(
            @Parameter(description = "ID único del curso a actualizar.", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO course
    ) {
        CourseDTO updatedCourse = courseService.update(id, course);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un curso",
            description = "Elimina un curso del sistema mediante su ID único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Curso eliminado exitosamente.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador del curso inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curso no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único del curso a eliminar.", example = "1")
            @PathVariable Long id
    ) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
