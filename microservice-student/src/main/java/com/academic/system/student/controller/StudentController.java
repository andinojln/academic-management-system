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
            summary = "Obtiene los datos de todos los estudiantes",
            description = "Busca en el sistema la lista de todos los " +
                    "estudiantes registrados, junto con sus datos. " +
                    "Si no hay registros, devuelve la lista vacía."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiantes encontrados correctamente.",
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
            summary = "Obtiene los datos de un estudiante por su ID",
            description = "Busca en el sistema el estudiante asociado al " +
                    "ID recibido, junto con sus datos. " +
                    "El ID será validado antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiante encontrado correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
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
    public ResponseEntity<StudentDTO> findById(
            @Parameter(description = "ID del estudiante.", example = "1")
            @PathVariable Long id
    ) {
        StudentDTO student = studentService.findById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping
    @Operation(
            summary = "Crea un nuevo estudiante",
            description = "Crea y registra un estudiante en el sistema con los datos recibidos. " +
                    "Los datos del estudiante serán validados antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Estudiante creado correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Los datos recibidos del estudiante no cumplen con las " +
                            "validaciones (formatos inválidos, datos obligatorios omitidos).",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya se encuentra registrado en el sistema.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<StudentDTO> save(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO savedStudent = studentService.save(studentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualiza los datos de un estudiante por su ID",
            description = "Busca en el sistema el estudiante asociado al ID recibido, " +
                    "junto con sus datos para realizar su actualización. " +
                    "El ID y los datos del estudiante serán validados antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiante actualizado correctamente.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudentDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El ID ingresado en la URL no es númerico de tipo (Long). " +
                            "O los datos recibidos del estudiante no cumplen con las " +
                            "validaciones (formatos inválidos, datos obligatorios omitidos).",
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya se encuentra registrado en el sistema.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)
                    )
            )
    })
    public ResponseEntity<StudentDTO> update(
            @Parameter(description = "ID del estudiante.", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StudentDTO studentDTO
    ) {
        StudentDTO updatedStudent = studentService.update(id, studentDTO);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Elimina un estudiante por su ID",
            description = "Busca en el sistema el estudiante asociado al ID recibido, " +
                    "para realizar su eliminación. " +
                    "El ID será validado antes de envíar la petición."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Estudiante eliminado correctamente.",
                    content = @Content // @Content vacío porque no hay body (204)
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
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del estudiante.", example = "1")
            @PathVariable Long id
    ) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
