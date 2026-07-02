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

    @GetMapping("/student/{studentId}")
    @Operation(
            summary = "Obtiene las inscripciones de un estudiante por su ID",
            description = "Busca en el sistema la lista de todas las " +
                    "inscripciones asociadas al ID recibido. " +
                    "Si el estudiante no existe devuelve un error 404, pero si " +
                    "existe y no tiene inscripciones, devuelve la lista vacía."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscripciones encontradas correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EnrollmentDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID ingresado en la URL no es númerico de tipo (Long).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El ID ingresado no corresponde a ningún estudiante registrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<List<EnrollmentDTO>> findAllByStudentId(
            @Parameter(description = "ID del estudiante.", example = "1")
            @PathVariable Long studentId
    ) {
        List<EnrollmentDTO> studentEnrollments = enrollmentService.findAllByStudentId(studentId);
        return ResponseEntity.ok(studentEnrollments);
    }

    @GetMapping("/course/{courseId}")
    @Operation(
            summary = "Obtiene las inscripciones de un curso por su ID",
            description = "Busca en el sistema la lista de todas las " +
                    "inscripciones asociadas al ID recibido. " +
                    "Si el curso no existe devuelve un error 404, pero si existe " +
                    "y no tiene inscripciones, devuelve la lista vacía."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscripciones encontradas correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = EnrollmentDTO.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID ingresado en la URL no es númerico de tipo (Long).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El ID ingresado no corresponde a ningún curso registrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<List<EnrollmentDTO>> findAllByCourseId(
            @Parameter(description = "ID del curso.", example = "1")
            @PathVariable Long courseId
    ) {
        List<EnrollmentDTO> courseEnrollments = enrollmentService.findAllByCourseId(courseId);
        return ResponseEntity.ok(courseEnrollments);
    }

    @PostMapping
    @Operation(
            summary = "Crea una nueva inscripción",
            description = "Crea y registra una inscripción en el sistema con los datos recibidos. " +
                    "Valida que el estudiante y el curso existan, y verifica que no haya una " +
                    "inscripción de un estudiante a un curso en el que ya se inscribió."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Inscripción creada correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnrollmentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Los datos recibidos de la inscripción no cumplen " +
                            "con las validaciones (datos obligatorios omitidos).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El ID del estudiante o el ID del curso, no " +
                            "existe o no está registrado en el sistema.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El estudiante ya se encuentra inscrito en el curso específicado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<EnrollmentDTO> save(@Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO savedEnrollment = enrollmentService.save(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollment);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualiza los datos de una inscripción por su ID",
            description = "Busca en el sistema la inscripción asociada al ID recibido, " +
                    "junto con sus datos para realizar su actualización. " +
                    "El ID y los datos de la inscripción serán validados antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscripción actualizada correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnrollmentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Los datos recibidos de la inscripción no cumplen " +
                            "con las validaciones (datos obligatorios omitidos). " +
                            "O el ID ingresado en la URL no es númerico de tipo (Long).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El ID de la inscripción, del estudiante o del curso, no " +
                            "existe o no está registrado en el sistema.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El estudiante ya se encuentra inscrito en el curso específicado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<EnrollmentDTO> update(
            @Parameter(description = "ID de la inscripción.", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentDTO enrollmentDTO
    ) {
        EnrollmentDTO updatedEnrollment = enrollmentService.update(id, enrollmentDTO);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Elimina una inscripción por su ID",
            description = "Busca en el sistema la inscripción asociada al ID recibido, " +
                    "para realizar su eliminación. " +
                    "El ID será validado antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Inscripción eliminada correctamente.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID ingresado en la URL no es númerico de tipo (Long).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El ID ingresado no corresponde a ninguna inscripción registrada.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la inscripción.", example = "1")
            @PathVariable Long id
    ) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
