package com.academic.system.student.service;

import com.academic.system.student.dto.StudentDTO;
import com.academic.system.student.entity.Student;
import com.academic.system.student.exception.ConflictException;
import com.academic.system.student.exception.ResourceNotFoundException;
import com.academic.system.student.mapper.StudentMapper;
import com.academic.system.student.repository.StudentRepository;
import com.academic.system.student.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    // ===== Patrón utilizado para los Tests Unitarios (Service): Given_When_Then ===== //

    // Dado un entorno - Cuando se ejecuta el
    // method - Entonces se verifica el resultado

    @Test
    @DisplayName("Debe retornar la lista de estudiantes cuando existan registros")
    void findAll_ShouldReturnStudentsList_WhenRecordsExist() {

        // ===== GIVEN ===== //

        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Santiago");
        student1.setLastName("Rossi");
        student1.setEmail("s.rossi@academia.com");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Valeria");
        student2.setLastName("Méndez");
        student2.setEmail("v.mendez@academia.com");

        List<Student> students = List.of(student1, student2);

        StudentDTO expectedStudent1 = new StudentDTO(
                student1.getId(),
                student1.getName(),
                student1.getLastName(),
                student1.getEmail());

        StudentDTO expectedStudent2 = new StudentDTO(
                student2.getId(),
                student2.getName(),
                student2.getLastName(),
                student2.getEmail());

        List<StudentDTO> expectedStudents = List.of(expectedStudent1, expectedStudent2);

        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toDTOList(students)).thenReturn(expectedStudents);

        // ===== WHEN ===== //

        List<StudentDTO> studentsResult = studentService.findAll();

        // ===== THEN ===== //

        assertThat(studentsResult)
                .isNotNull()
                .hasSize(2)
                .containsExactlyElementsOf(expectedStudents);

        verify(studentRepository, times(1)).findAll();
        verify(studentMapper, times(1)).toDTOList(students);
    }

    @Test
    @DisplayName("Debe retornar la lista vacía cuando no existan registros")
    void findAll_ShouldReturnEmptyList_WhenNoRecordsExist() {

        // ===== GIVEN ===== //

        List<Student> emptyList = List.of();

        when(studentRepository.findAll()).thenReturn(emptyList);
        when(studentMapper.toDTOList(emptyList)).thenReturn(List.of());

        // ===== WHEN ===== //

        List<StudentDTO> studentsResult = studentService.findAll();

        // ===== THEN ===== //

        assertThat(studentsResult)
                .isNotNull()
                .isEmpty();

        verify(studentRepository, times(1)).findAll();
        verify(studentMapper, times(1)).toDTOList(emptyList);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar el estudiante cuando exista")
    void findById_ShouldReturnStudent_WhenStudentExists() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        Student student = new Student();
        student.setId(studentId);
        student.setName("Santiago");
        student.setLastName("Rossi");
        student.setEmail("s.rossi@academia.com");

        StudentDTO expectedStudent = new StudentDTO(
                student.getId(),
                student.getName(),
                student.getLastName(),
                student.getEmail());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentMapper.toDTO(student)).thenReturn(expectedStudent);

        // ===== WHEN ===== //

        StudentDTO studentResult = studentService.findById(studentId);

        // ===== THEN ===== //

        assertThat(studentResult)
                .isNotNull()
                .isEqualTo(expectedStudent);

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentMapper, times(1)).toDTO(student);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el estudiante no exista")
    void findById_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentService.findById(studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Estudiante no encontrado con el ID: " + studentId);

        verify(studentRepository, times(1)).findById(studentId);
        verifyNoInteractions(studentMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe guardar y retornar el estudiante cuando el email no esté registrado")
    void save_ShouldReturnStudent_WhenEmailIsNotRegistered() {

        // ===== GIVEN ===== //

        StudentDTO inputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "s.rossi@academia.com");

        Student studentToSave = new Student();
        studentToSave.setName(inputStudent.name());
        studentToSave.setLastName(inputStudent.lastName());
        studentToSave.setEmail(inputStudent.email());

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName(studentToSave.getName());
        savedStudent.setLastName(studentToSave.getLastName());
        savedStudent.setEmail(studentToSave.getEmail());

        StudentDTO expectedStudent = new StudentDTO(
                savedStudent.getId(),
                savedStudent.getName(),
                savedStudent.getLastName(),
                savedStudent.getEmail());

        when(studentRepository.existsByEmail(inputStudent.email())).thenReturn(false);
        when(studentMapper.toEntity(inputStudent)).thenReturn(studentToSave);
        when(studentRepository.save(studentToSave)).thenReturn(savedStudent);
        when(studentMapper.toDTO(savedStudent)).thenReturn(expectedStudent);

        // ===== WHEN ===== //

        StudentDTO studentResult = studentService.save(inputStudent);

        // ===== THEN ===== //

        assertThat(studentResult)
                .isNotNull()
                .isEqualTo(expectedStudent);

        verify(studentRepository, times(1)).existsByEmail(inputStudent.email());
        verify(studentMapper, times(1)).toEntity(inputStudent);
        verify(studentRepository, times(1)).save(studentToSave);
        verify(studentMapper, times(1)).toDTO(savedStudent);
    }

    @Test
    @DisplayName("Debe lanzar (ConflictException) cuando el email ya esté registrado")
    void save_ShouldThrowConflictException_WhenEmailIsAlreadyRegistered() {

        // ===== GIVEN ===== //

        StudentDTO inputStudent = new StudentDTO(
                null,
                "Santiago",
                "Rossi",
                "v.mendez@academia.com"); // Pertenece a otro estudiante registrado

        when(studentRepository.existsByEmail(inputStudent.email())).thenReturn(true);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentService.save(inputStudent))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El email '" + inputStudent.email() + "' ya se encuentra registrado");

        verify(studentRepository, times(1)).existsByEmail(inputStudent.email());
        verify(studentRepository, never()).save(any(Student.class));
        verifyNoInteractions(studentMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe actualizar y retornar el estudiante cuando el email no esté registrado")
    void update_ShouldReturnStudent_WhenEmailIsNotRegistered() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setName("Santiago");
        existingStudent.setLastName("Rossi");
        existingStudent.setEmail("s.rossi@academia.com");

        StudentDTO inputStudent = new StudentDTO(
                studentId,
                "updated__Santiago",
                "updated__Rossi",
                "updated__s.rossi@academia.com");

        StudentDTO expectedStudent = new StudentDTO(
                inputStudent.id(),
                inputStudent.name(),
                inputStudent.lastName(),
                inputStudent.email());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.existsByEmail(inputStudent.email())).thenReturn(false);
        when(studentRepository.save(existingStudent)).thenReturn(existingStudent);
        when(studentMapper.toDTO(existingStudent)).thenReturn(expectedStudent);

        // ===== WHEN ===== //

        StudentDTO studentResult = studentService.update(studentId, inputStudent);

        // ===== THEN ===== //

        assertThat(studentResult)
                .isNotNull()
                .isEqualTo(expectedStudent);

        // Verifica la mutación de estado en la entidad existente antes de persistir
        assertThat(existingStudent.getName()).isEqualTo(inputStudent.name());
        assertThat(existingStudent.getLastName()).isEqualTo(inputStudent.lastName());
        assertThat(existingStudent.getEmail()).isEqualTo(inputStudent.email());

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).existsByEmail(inputStudent.email());
        verify(studentRepository, times(1)).save(existingStudent);
        verify(studentMapper, times(1)).toDTO(existingStudent);
    }

    @Test
    @DisplayName("Debe actualizar y retornar el estudiante cuando el email no sea modificado")
    void update_ShouldReturnStudent_WhenEmailIsNotModified() {

        // ===== GIVEN ===== //

        Long studentId = 1L;
        String sameEmail = "s.rossi@academia.com";

        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setName("Santiago");
        existingStudent.setLastName("Rossi");
        existingStudent.setEmail(sameEmail);

        // Solo cambian el nombre y apellido, el email sigue siendo el mismo
        StudentDTO inputStudent = new StudentDTO(
                studentId,
                "updated__Santiago",
                "updated__Rossi",
                sameEmail);

        StudentDTO expectedStudent = new StudentDTO(
                inputStudent.id(),
                inputStudent.name(),
                inputStudent.lastName(),
                inputStudent.email());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(existingStudent)).thenReturn(existingStudent);
        when(studentMapper.toDTO(existingStudent)).thenReturn(expectedStudent);

        // ===== WHEN ===== //

        StudentDTO studentResult = studentService.update(studentId, inputStudent);

        // ===== THEN ===== //

        assertThat(studentResult)
                .isNotNull()
                .isEqualTo(expectedStudent);

        // Verifica la mutación de estado en la entidad existente antes de persistir
        assertThat(existingStudent.getName()).isEqualTo(inputStudent.name());
        assertThat(existingStudent.getLastName()).isEqualTo(inputStudent.lastName());

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, never()).existsByEmail(anyString());
        verify(studentRepository, times(1)).save(existingStudent);
        verify(studentMapper, times(1)).toDTO(existingStudent);
    }

    @Test
    @DisplayName("Debe lanzar (ConflictException) cuando el email ya esté registrado")
    void update_ShouldThrowConflictException_WhenEmailIsAlreadyRegistered() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setName("Santiago");
        existingStudent.setLastName("Rossi");
        existingStudent.setEmail("s.rossi@academia.com");

        StudentDTO inputStudent = new StudentDTO(
                studentId,
                existingStudent.getName(),
                existingStudent.getLastName(),
                "v.mendez@academia.com"); // Pertenece a otro estudiante registrado

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.existsByEmail(inputStudent.email())).thenReturn(true);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentService.update(studentId, inputStudent))
                .isInstanceOf(ConflictException.class)
                .hasMessage("El email '" + inputStudent.email() + "' ya se encuentra registrado");

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).existsByEmail(inputStudent.email());
        verify(studentRepository, never()).save(any(Student.class));
        verifyNoInteractions(studentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el estudiante no exista")
    void update_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        StudentDTO inputStudent = new StudentDTO(
                studentId,
                "updated__Santiago",
                "updated__Rossi",
                "updated__s.rossi@academia.com");

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentService.update(studentId, inputStudent))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Estudiante no encontrado con el ID: " + studentId);

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, never()).existsByEmail(anyString());
        verify(studentRepository, never()).save(any(Student.class));
        verifyNoInteractions(studentMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe eliminar el estudiante cuando exista")
    void delete_ShouldDeleteStudent_WhenStudentExists() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        when(studentRepository.existsById(studentId)).thenReturn(true);

        // ===== WHEN & THEN ===== //

        assertThatNoException().isThrownBy(() -> studentService.delete(studentId));

        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, times(1)).deleteById(studentId);
        verifyNoInteractions(studentMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el estudiante no exista")
    void delete_ShouldThrowResourceNotFoundException_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long studentId = 1L;

        when(studentRepository.existsById(studentId)).thenReturn(false);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentService.delete(studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Estudiante no encontrado con el ID: " + studentId);

        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, never()).deleteById(anyLong());
        verifyNoInteractions(studentMapper);
    }
}
