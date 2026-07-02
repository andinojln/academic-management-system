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
            summary = "Obtiene los datos de todos los cursos",
            description = "Busca en el sistema la lista de todos los " +
                    "cursos registrados, junto con sus datos. " +
                    "Si no hay registros, devuelve la lista vacía."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cursos encontrados correctamente.",
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
            summary = "Obtiene los datos de un curso por su ID",
            description = "Busca en el sistema el curso asociado al " +
                    "ID recibido, junto con sus datos. " +
                    "El ID será validado antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Curso encontrado correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.class)
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
    public ResponseEntity<CourseDTO> findById(
            @Parameter(description = "ID del curso.", example = "1")
            @PathVariable Long id
    ) {
        CourseDTO course = courseService.findById(id);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @Operation(
            summary = "Crea un nuevo curso",
            description = "Crea y registra un curso en el sistema con los datos recibidos. " +
                    "Los datos del curso serán validados antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Curso creado correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Los datos recibidos del curso no cumplen con las " +
                            "validaciones (datos obligatorios omitidos).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<CourseDTO> save(@Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO savedCourse = courseService.save(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualiza los datos de un curso por su ID",
            description = "Busca en el sistema el curso asociado al ID recibido, " +
                    "junto con sus datos para realizar su actualización. " +
                    "El ID y los datos del curso serán validados antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Curso actualizado correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID ingresado en la URL no es númerico de tipo (Long). " +
                            "O los datos recibidos del curso no cumplen con las " +
                            "validaciones (datos obligatorios omitidos).",
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
    public ResponseEntity<CourseDTO> update(
            @Parameter(description = "ID del curso.", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO
    ) {
        CourseDTO updatedCourse = courseService.update(id, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Elimina un curso por su ID",
            description = "Busca en el sistema el curso asociado al ID recibido, " +
                    "para realizar su eliminación. " +
                    "El ID será validado antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Curso eliminado correctamente.",
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
                    description = "El ID ingresado no corresponde a ningún curso registrado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del curso.", example = "1")
            @PathVariable Long id
    ) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
