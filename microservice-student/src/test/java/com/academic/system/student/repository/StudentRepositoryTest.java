package com.academic.system.student.repository;

import com.academic.system.student.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ===== Patrón utilizado para los Tests de Integración (Data): Given_When_Then ===== //

    @Test
    @DisplayName("Debe retornar todos los estudiantes cuando existan registros")
    void findAll_ShouldReturnAllStudents_WhenRecordsExist() {

        // ===== GIVEN ===== //

        Student student1 = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student student2 = Student.builder()
                .name("Valeria")
                .lastName("Méndez")
                .email("v.mendez@academia.com")
                .build();

        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.clear(); // Limpia el caché del EntityManager

        // ===== WHEN ===== //

        List<Student> students = studentRepository.findAll();

        // ===== THEN ===== //

        assertThat(students)
                .isNotNull()
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(List.of(student1, student2));
    }

    @Test
    @DisplayName("Debe retornar una lista vacía cuando no existan registros")
    void findAll_ShouldReturnEmptyList_WhenNoRecordsExist() {

        // ===== GIVEN ===== //

        // ===== WHEN ===== //

        List<Student> students = studentRepository.findAll();

        // ===== THEN ===== //

        assertThat(students)
                .isNotNull()
                .isEmpty();
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar el estudiante en un Optional cuando exista")
    void findById_ShouldReturnStudent_WhenStudentExists() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student savedStudent = entityManager.persistAndFlush(student);
        entityManager.clear();

        // ===== WHEN ===== //

        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getId());

        // ===== THEN ===== //

        assertThat(foundStudent)
                .isPresent();

        assertThat(foundStudent.get())
                .usingRecursiveComparison()
                .isEqualTo(savedStudent);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el estudiante no exista")
    void findById_ShouldReturnEmptyOptional_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long nonExistingId = 999L;

        // ===== WHEN ===== //

        Optional<Student> foundStudent = studentRepository.findById(nonExistingId);

        // ===== THEN ===== //

        assertThat(foundStudent).isEmpty();
    }

    // ====================================== //

    @Test
    @DisplayName("Debe guardar y retornar el estudiante cuando el email no esté registrado")
    void save_ShouldSaveStudent_WhenEmailIsNotRegistered() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        // ===== WHEN ===== //

        Student savedStudent = studentRepository.save(student);
        entityManager.flush(); // Fuerza a H2 a ejecutar el INSERT
        entityManager.clear(); // Limpia el caché de primer nivel

        Student foundStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId()).isNotNull(); // Verifica que la DB le generó un ID

        assertThat(foundStudent)
                .usingRecursiveComparison()
                .isEqualTo(savedStudent);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al guardar cuando el nombre sea null")
    void save_ShouldThrowDataIntegrityViolationException_WhenNameIsNull() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name(null)
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(student))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al guardar cuando el apellido sea null")
    void save_ShouldThrowDataIntegrityViolationException_WhenLastNameIsNull() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName(null)
                .email("s.rossi@academia.com")
                .build();

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(student))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al guardar cuando el email sea null")
    void save_ShouldThrowDataIntegrityViolationException_WhenEmailIsNull() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email(null)
                .build();

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(student))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) " +
            "al guardar cuando el email ya esté registrado")
    void save_ShouldThrowDataIntegrityViolationException_WhenEmailIsAlreadyRegistered() {

        // ===== GIVEN ===== //

        Student student1 = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        entityManager.persistAndFlush(student1);

        Student student2 = Student.builder()
                .name("Valeria")
                .lastName("Méndez")
                .email("s.rossi@academia.com")
                .build();

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(student2)) // Acción esperada a fallar
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe actualizar y retornar el estudiante cuando el email no esté registrado")
    void update_ShouldUpdateStudent_WhenEmailIsNotRegistered() {

        // ===== GIVEN ===== //

        Student existingStudent = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student savedStudent = entityManager.persistAndFlush(existingStudent);
        entityManager.clear();

        savedStudent.setName("updated__Santiago");
        savedStudent.setLastName("updated__Rossi");
        savedStudent.setEmail("updated__s.rossi@academia.com");

        // ===== WHEN ===== //

        // Al tener ID, JPA ejecuta un UPDATE en vez de INSERT
        Student updatedStudent = studentRepository.save(savedStudent);
        entityManager.flush();
        entityManager.clear();

        Student foundStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(updatedStudent).isNotNull();
        assertThat(updatedStudent.getId()).isEqualTo(savedStudent.getId());

        assertThat(foundStudent)
                .usingRecursiveComparison()
                .isEqualTo(updatedStudent);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al actualizar cuando el nombre sea null")
    void update_ShouldThrowDataIntegrityViolationException_WhenNameToUpdateIsNull() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student savedStudent = entityManager.persistAndFlush(student);
        entityManager.clear();

        savedStudent.setName(null);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(student))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al actualizar cuando el apellido sea null")
    void update_ShouldThrowDataIntegrityViolationException_WhenLastNameToUpdateIsNull() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student savedStudent = entityManager.persistAndFlush(student);
        entityManager.clear();

        savedStudent.setLastName(null);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(student))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al actualizar cuando el email sea null")
    void update_ShouldThrowDataIntegrityViolationException_WhenEmailToUpdateIsNull() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student savedStudent = entityManager.persistAndFlush(student);
        entityManager.clear();

        savedStudent.setEmail(null);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(student))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) " +
            "al actualizar cuando el email ya esté registrado")
    void update_ShouldThrowDataIntegrityViolationException_WhenEmailToUpdateIsAlreadyRegistered() {

        // ===== GIVEN ===== //

        Student student1 = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student student2 = Student.builder()
                .name("Valeria")
                .lastName("Méndez")
                .email("v.mendez@academia.com")
                .build();

        entityManager.persistAndFlush(student1);
        Student savedStudent2 = entityManager.persistAndFlush(student2);
        entityManager.clear();

        savedStudent2.setEmail("s.rossi@academia.com");

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> studentRepository.saveAndFlush(savedStudent2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe eliminar el estudiante cuando exista")
    void delete_ShouldDeleteStudent_WhenStudentExists() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        Student savedStudent = entityManager.persistAndFlush(student);
        entityManager.clear();

        // ===== WHEN ===== //

        studentRepository.deleteById(savedStudent.getId());
        entityManager.flush(); // Fuerza la ejecución inmediata de SQL DELETE
        entityManager.clear(); // Limpia nuevamente antes de verificar la consulta

        // Verifica que al buscar el ID en H2, devuelva un Optional vacío
        Optional<Student> deletedStudent = studentRepository.findById(savedStudent.getId());

        // ===== THEN ===== //

        assertThat(deletedStudent).isEmpty();
    }

    @Test
    @DisplayName("No debe lanzar excepción ni eliminar registros existentes cuando el estudiante no exista")
    void delete_ShouldDoNothing_WhenStudentDoesNotExist() {

        // ===== GIVEN ===== //

        Long nonExistingId = 999L;

        Student existingStudent = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        entityManager.persistAndFlush(existingStudent);
        entityManager.clear();

        // ===== WHEN & THEN ===== //

        assertThatCode(() -> studentRepository.deleteById(nonExistingId))
                .doesNotThrowAnyException();

        entityManager.flush();

        // Verifica que los registros existentes en la tabla permanezcan intactos
        assertThat(studentRepository.count()).isEqualTo(1);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar (true) cuando el email exista")
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {

        // ===== GIVEN ===== //

        Student student = Student.builder()
                .name("Santiago")
                .lastName("Rossi")
                .email("s.rossi@academia.com")
                .build();

        entityManager.persistAndFlush(student);
        entityManager.clear();

        // ===== WHEN ===== //

        // Consulta si el email existe mediante el custom method
        boolean exists = studentRepository.existsByEmail(student.getEmail());

        // ===== THEN ===== //

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe retornar (false) cuando el email no exista")
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {

        // ===== GIVEN ===== //

        String nonExistingEmail = "not.exists@academia.com";

        // ===== WHEN ===== //

        boolean exists = studentRepository.existsByEmail(nonExistingEmail);

        // ===== THEN ===== //

        assertThat(exists).isFalse();
    }
}
