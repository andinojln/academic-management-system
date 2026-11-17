package com.academic.system.enrollment.controller;

import com.academic.system.enrollment.dto.response.EnrollmentDTO;
import com.academic.system.enrollment.exception.ErrorDTO;
import com.academic.system.enrollment.service.EnrollmentService;
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
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(
        name = "Inscripciones",
        description = "Endpoints para la gestión de inscripciones del Sistema Académico."
)
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener datos de una inscripción",
            description = "Devuelve los datos de una inscripción específica mediante su ID " +
                    "único, enriqueciendo la respuesta con los datos del estudiante y del curso."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscripción recuperada exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnrollmentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador de la inscripción inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inscripción no encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<EnrollmentDTO> findById(
            @Parameter(description = "ID único de la inscripción a consultar.", example = "1")
            @PathVariable Long id
    ) {
        EnrollmentDTO enrollment = enrollmentService.findById(id);
        return ResponseEntity.ok(enrollment);
    }

    @GetMapping("/student/{studentId}")
    @Operation(
            summary = "Obtener inscripciones por estudiante",
            description = "Devuelve el listado de todas las inscripciones registradas de un " +
                    "estudiante específico, incluyendo los datos enriquecidos de los cursos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscripciones recuperadas exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EnrollmentDTO.class)
                            )
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
    public ResponseEntity<List<EnrollmentDTO>> findAllByStudentId(
            @Parameter(description = "ID único del estudiante a consultar.", example = "1")
            @PathVariable Long studentId
    ) {
        List<EnrollmentDTO> studentEnrollments = enrollmentService.findAllByStudentId(studentId);
        return ResponseEntity.ok(studentEnrollments);
    }

    @GetMapping("/course/{courseId}")
    @Operation(
            summary = "Obtener inscripciones por curso",
            description = "Devuelve el listado de todas las inscripciones registradas de un " +
                    "curso específico, incluyendo los datos enriquecidos de los estudiantes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscripciones recuperadas exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EnrollmentDTO.class)
                            )
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
    public ResponseEntity<List<EnrollmentDTO>> findAllByCourseId(
            @Parameter(description = "ID único del curso a consultar.", example = "1")
            @PathVariable Long courseId
    ) {
        List<EnrollmentDTO> courseEnrollments = enrollmentService.findAllByCourseId(courseId);
        return ResponseEntity.ok(courseEnrollments);
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva inscripción",
            description = "Registra una nueva inscripción en el sistema asociando un " +
                    "estudiante con un curso, tras validar la existencia de ambas " +
                    "entidades y la inexistencia de duplicados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Inscripción registrada exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnrollmentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de la inscripción inválidos o incompletos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante o curso no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Estudiante ya inscripto en el curso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<EnrollmentDTO> save(@Valid @RequestBody EnrollmentDTO enrollment) {

        EnrollmentDTO savedEnrollment = enrollmentService.save(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollment);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una inscripción",
            description = "Actualiza la información de una inscripción existente, revalidando la " +
                    "existencia del estudiante y del curso, así como la regla de no duplicados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscripción actualizada exitosamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnrollmentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador inválido o datos de la inscripción incompletos.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inscripción, estudiante o curso no encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Estudiante ya inscripto en el curso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<EnrollmentDTO> update(
            @Parameter(description = "ID único de la inscripción a actualizar.", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentDTO enrollment
    ) {
        EnrollmentDTO updatedEnrollment = enrollmentService.update(id, enrollment);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una inscripción",
            description = "Elimina una inscripción del sistema mediante su ID único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Inscripción eliminada exitosamente.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Identificador de la inscripción inválido.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inscripción no encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único de la inscripción a eliminar.", example = "1")
            @PathVariable Long id
    ) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
