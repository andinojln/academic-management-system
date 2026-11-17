package com.academic.system.enrollment.repository;

import com.academic.system.enrollment.entity.Enrollment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
public class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ===== Patrón utilizado para los Tests de Integración (Data): Given_When_Then ===== //

    @Test
    @DisplayName("Debe retornar la inscripción en un Optional cuando exista")
    void findById_ShouldReturnEnrollment_WhenEnrollmentExists() {

        // ===== GIVEN ===== //

        Enrollment enrollment = Enrollment.builder()
                .studentId(10L)
                .courseId(20L)
                .build();

        Enrollment savedEnrollment = entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        // ===== WHEN ===== //

        Optional<Enrollment> foundEnrollment = enrollmentRepository.findById(savedEnrollment.getId());

        // ===== THEN ===== //

        assertThat(foundEnrollment)
                .isPresent();

        assertThat(foundEnrollment.get())
                .usingRecursiveComparison()
                .isEqualTo(savedEnrollment);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando la inscripción no exista")
    void findById_ShouldReturnEmptyOptional_WhenEnrollmentDoesNotExist() {

        // ===== GIVEN ===== //

        Long nonExistingId = 999L;

        // ===== WHEN ===== //

        Optional<Enrollment> foundEnrollment = enrollmentRepository.findById(nonExistingId);

        // ===== THEN ===== //

        assertThat(foundEnrollment).isEmpty();
    }

    // ====================================== //

    @Test
    @DisplayName("Debe guardar y retornar la inscripción cuando los campos requeridos estén presentes")
    void save_ShouldSaveEnrollment_WhenRequiredFieldsArePresent() {

        // ===== GIVEN ===== //

        Enrollment enrollment = Enrollment.builder()
                .studentId(10L)
                .courseId(20L)
                .build();

        // ===== WHEN ===== //

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        entityManager.flush();
        entityManager.clear();

        Enrollment foundEnrollment = enrollmentRepository.findById(savedEnrollment.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(savedEnrollment).isNotNull();
        assertThat(savedEnrollment.getId()).isNotNull();

        assertThat(foundEnrollment)
                .usingRecursiveComparison()
                .isEqualTo(savedEnrollment);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al guardar cuando el studentId sea null")
    void save_ShouldThrowDataIntegrityViolationException_WhenStudentIdIsNull() {

        // ===== GIVEN ===== //

        Enrollment enrollment = Enrollment.builder()
                .studentId(null)
                .courseId(20L)
                .build();

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentRepository.saveAndFlush(enrollment))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al guardar cuando el courseId sea null")
    void save_ShouldThrowDataIntegrityViolationException_WhenCourseIdIsNull() {

        // ===== GIVEN ===== //

        Enrollment enrollment = Enrollment.builder()
                .studentId(10L)
                .courseId(null)
                .build();

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentRepository.saveAndFlush(enrollment))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe actualizar y retornar la inscripción cuando los campos requeridos estén presentes")
    void update_ShouldUpdateEnrollment_WhenRequiredFieldsArePresent() {

        // ===== GIVEN ===== //

        Enrollment existingEnrollment = Enrollment.builder()
                .studentId(10L)
                .courseId(20L)
                .build();

        Enrollment savedEnrollment = entityManager.persistAndFlush(existingEnrollment);
        entityManager.clear();

        savedEnrollment.setStudentId(11L);
        savedEnrollment.setCourseId(21L);

        // ===== WHEN ===== //

        // Al tener ID, JPA ejecuta un UPDATE en vez de INSERT
        Enrollment updatedEnrollment = enrollmentRepository.save(savedEnrollment);
        entityManager.flush();
        entityManager.clear();

        Enrollment foundEnrollment = enrollmentRepository.findById(updatedEnrollment.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(updatedEnrollment).isNotNull();
        assertThat(updatedEnrollment.getId()).isEqualTo(savedEnrollment.getId());

        assertThat(foundEnrollment)
                .usingRecursiveComparison()
                .isEqualTo(updatedEnrollment);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al actualizar cuando el studentId sea null")
    void update_ShouldThrowDataIntegrityViolationException_WhenStudentIdIsNull() {

        // ===== GIVEN ===== //

        Enrollment enrollment = Enrollment.builder()
                .studentId(10L)
                .courseId(20L)
                .build();

        Enrollment savedEnrollment = entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        savedEnrollment.setStudentId(null);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentRepository.saveAndFlush(savedEnrollment))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al actualizar cuando el courseId sea null")
    void update_ShouldThrowDataIntegrityViolationException_WhenCourseIdIsNull() {

        // ===== GIVEN ===== //

        Enrollment enrollment = Enrollment.builder()
                .studentId(10L)
                .courseId(20L)
                .build();

        Enrollment savedEnrollment = entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        savedEnrollment.setCourseId(null);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> enrollmentRepository.saveAndFlush(savedEnrollment))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe eliminar la inscripción cuando exista")
    void delete_ShouldDeleteEnrollment_WhenEnrollmentExists() {

        // ===== GIVEN ===== //

        Enrollment enrollment = Enrollment.builder()
                .studentId(10L)
                .courseId(20L)
                .build();

        Enrollment savedEnrollment = entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        // ===== WHEN ===== //

        enrollmentRepository.deleteById(savedEnrollment.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Enrollment> deletedEnrollment = enrollmentRepository.findById(savedEnrollment.getId());

        // ===== THEN ===== //

        assertThat(deletedEnrollment).isEmpty();
    }

    @Test
    @DisplayName("No debe lanzar excepción ni eliminar registros existentes cuando la inscripción no exista")
    void delete_ShouldDoNothing_WhenEnrollmentDoesNotExist() {

        // ===== GIVEN ===== //

        Long nonExistingId = 999L;

        Enrollment existingEnrollment = new Enrollment();
        existingEnrollment.setStudentId(10L);
        existingEnrollment.setCourseId(20L);

        entityManager.persistAndFlush(existingEnrollment);
        entityManager.clear();

        // ===== WHEN & THEN ===== //

        assertThatCode(() -> enrollmentRepository.deleteById(nonExistingId))
                .doesNotThrowAnyException();

        entityManager.flush();

        // Verifica que los registros existentes en la tabla permanezcan intactos
        assertThat(enrollmentRepository.count()).isEqualTo(1);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar la lista de inscripciones cuando el estudiante tenga inscripciones")
    void findAllByStudentId_ShouldReturnEnrollments_WhenStudentHasEnrollments() {

        // ===== GIVEN ===== //

        Long targetStudentId = 1L;

        Enrollment enrollment1 = Enrollment.builder()
                .studentId(targetStudentId)
                .courseId(100L)
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .studentId(targetStudentId)
                .courseId(101L)
                .build();

        // Persiste una inscripción para el estudiante (2L), para verificar que la consulta
        // SQL aplique el filtro WHERE y no devuelva las inscripciones de este
        Enrollment otherStudentEnrollment = Enrollment.builder()
                .studentId(2L)
                .courseId(102L)
                .build();

        entityManager.persist(enrollment1);
        entityManager.persist(enrollment2);
        entityManager.persist(otherStudentEnrollment);
        entityManager.flush();
        entityManager.clear();

        // ===== WHEN ===== //

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(targetStudentId);

        // ===== THEN ===== //

        assertThat(enrollments).hasSize(2);

        assertThat(enrollments)
                .extracting(Enrollment::getStudentId)
                .containsOnly(targetStudentId);

        assertThat(enrollments)
                .extracting(Enrollment::getCourseId)
                .containsExactlyInAnyOrder(100L, 101L);
    }

    @Test
    @DisplayName("Debe retornar la lista vacía cuando el estudiante no tenga inscripciones")
    void findAllByStudentId_ShouldReturnEmptyList_WhenStudentHasNoEnrollments() {

        // ===== GIVEN ===== //

        Long nonExistingStudentId = 999L;

        // ===== WHEN ===== //

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(nonExistingStudentId);

        // ===== THEN ===== //

        assertThat(enrollments).isNotNull();
        assertThat(enrollments).isEmpty();
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar la lista de inscripciones cuando el curso tenga inscripciones")
    void findAllByCourseId_ShouldReturnEnrollments_WhenCourseHasEnrollments() {

        // ===== GIVEN ===== //

        Long targetCourseId = 100L;

        Enrollment enrollment1 = Enrollment.builder()
                .studentId(1L)
                .courseId(targetCourseId)
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .studentId(2L)
                .courseId(targetCourseId)
                .build();

        Enrollment otherCourseEnrollment = Enrollment.builder()
                .studentId(3L)
                .courseId(200L)
                .build();

        entityManager.persist(enrollment1);
        entityManager.persist(enrollment2);
        entityManager.persist(otherCourseEnrollment);
        entityManager.flush();
        entityManager.clear();

        // ===== WHEN ===== //

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(targetCourseId);

        // ===== THEN ===== //

        assertThat(enrollments).hasSize(2);

        assertThat(enrollments)
                .extracting(Enrollment::getCourseId)
                .containsOnly(targetCourseId);

        assertThat(enrollments)
                .extracting(Enrollment::getStudentId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("Debe retornar la lista vacía cuando el curso no tenga inscripciones")
    void findAllByCourseId_ShouldReturnEmptyList_WhenCourseHasNoEnrollments() {

        // ===== GIVEN ===== //

        Long nonExistingCourseId = 999L;

        // ===== WHEN ===== //

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(nonExistingCourseId);

        // ===== THEN ===== //

        assertThat(enrollments).isNotNull();
        assertThat(enrollments).isEmpty();
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar (true) cuando exista una inscripción para el estudiante y curso indicados")
    void existsByStudentIdAndCourseId_ShouldReturnTrue_WhenEnrollmentExists() {

        // ===== GIVEN ===== //

        Long targetStudentId = 1L;
        Long targetCourseId = 100L;

        Enrollment enrollment = Enrollment.builder()
                .studentId(targetStudentId)
                .courseId(targetCourseId)
                .build();

        entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        // ===== WHEN ===== //

        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(targetStudentId, targetCourseId);

        // ===== THEN ===== //

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe retornar (false) cuando el estudiante esté inscrito en otro curso diferente")
    void existsByStudentIdAndCourseId_ShouldReturnFalse_WhenStudentIsEnrolledInDifferentCourse() {

        // ===== GIVEN ===== //

        Long targetStudentId = 1L;
        Long enrolledCourseId = 100L;
        Long nonEnrolledCourseId = 200L;

        Enrollment enrollment = Enrollment.builder()
                .studentId(targetStudentId)
                .courseId(enrolledCourseId)
                .build();

        entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        // ===== WHEN ===== //

        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(targetStudentId, nonEnrolledCourseId);

        // ===== THEN ===== //

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe retornar (false) cuando el curso tenga inscrito a otro estudiante diferente")
    void existsByStudentIdAndCourseId_ShouldReturnFalse_WhenCourseHasDifferentStudent() {

        // ===== GIVEN ===== //

        Long enrolledStudentId = 1L;
        Long nonEnrolledStudentId = 2L;
        Long targetCourseId = 100L;

        Enrollment enrollment = Enrollment.builder()
                .studentId(enrolledStudentId)
                .courseId(targetCourseId)
                .build();

        entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        // ===== WHEN ===== //

        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(nonEnrolledStudentId, targetCourseId);

        // ===== THEN ===== //

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Debe retornar (false) cuando no exista una inscripcion para el estudiante y curso indicados")
    void existsByStudentIdAndCourseId_ShouldReturnFalse_WhenEnrollmentDoesNotExist() {

        // ===== GIVEN ===== //

        Long nonExistingStudentId = 999L;
        Long nonExistingCourseId = 999L;

        // ===== WHEN ===== //

        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(
                nonExistingStudentId, nonExistingCourseId);

        // ===== THEN ===== //

        assertThat(exists).isFalse();
    }
}
