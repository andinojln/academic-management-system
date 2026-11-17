package com.academic.system.course.repository;

import com.academic.system.course.entity.Course;
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
public class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ===== Patrón utilizado para los Tests de Integración (Data): Given_When_Then ===== //

    @Test
    @DisplayName("Debe retornar todos los cursos cuando existan registros")
    void findAll_ShouldReturnAllCourses_WhenRecordsExist() {

        // ===== GIVEN ===== //

        Course course1 = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        Course course2 = Course.builder()
                .name("Programación Reactiva")
                .teacher("Dra. Marina")
                .build();

        entityManager.persistAndFlush(course1);
        entityManager.persistAndFlush(course2);
        entityManager.clear();

        // ===== WHEN ===== //

        List<Course> courses = courseRepository.findAll();

        // ===== THEN ===== //

        assertThat(courses)
                .isNotNull()
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(List.of(course1, course2));
    }

    @Test
    @DisplayName("Debe retornar una lista vacía cuando no existan registros")
    void findAll_ShouldReturnEmptyList_WhenNoRecordsExist() {

        // ===== GIVEN ===== //

        // ===== WHEN ===== //

        List<Course> courses = courseRepository.findAll();

        // ===== THEN ===== //

        assertThat(courses)
                .isNotNull()
                .isEmpty();
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar el curso en un Optional cuando exista")
    void findById_ShouldReturnCourse_WhenCourseExists() {

        // ===== GIVEN ===== //

        Course course = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        Course savedCourse = entityManager.persistAndFlush(course);
        entityManager.clear();

        // ===== WHEN ===== //

        Optional<Course> foundCourse = courseRepository.findById(savedCourse.getId());

        // ===== THEN ===== //

        assertThat(foundCourse)
                .isPresent();

        assertThat(foundCourse.get())
                .usingRecursiveComparison()
                .isEqualTo(savedCourse);
    }

    @Test
    @DisplayName("Debe retornar un Optional vacío cuando el curso no exista")
    void findById_ShouldReturnEmptyOptional_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long nonExistingId = 999L;

        // ===== WHEN ===== //

        Optional<Course> foundCourse = courseRepository.findById(nonExistingId);

        // ===== THEN ===== //

        assertThat(foundCourse).isEmpty();
    }

    // ====================================== //

    @Test
    @DisplayName("Debe guardar y retornar el curso cuando los campos requeridos estén presentes")
    void save_ShouldSaveCourse_WhenRequiredFieldsArePresent() {

        // ===== GIVEN ===== //

        Course course = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        // ===== WHEN ===== //

        Course savedCourse = courseRepository.save(course);
        entityManager.flush();
        entityManager.clear();

        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getId()).isNotNull();

        assertThat(foundCourse)
                .usingRecursiveComparison()
                .isEqualTo(savedCourse);
    }

    @Test
    @DisplayName("Debe guardar y retornar el curso cuando el profesor sea null (campo opcional)")
    void save_ShouldSaveCourse_WhenTeacherIsNull() {

        // ===== GIVEN ===== //

        Course course = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher(null)
                .build();

        // ===== WHEN ===== //

        Course savedCourse = courseRepository.save(course);
        entityManager.flush();
        entityManager.clear();

        Course foundCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getId()).isNotNull();

        assertThat(foundCourse)
                .usingRecursiveComparison()
                .isEqualTo(savedCourse);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al guardar cuando el nombre sea null")
    void save_ShouldThrowDataIntegrityViolationException_WhenNameIsNull() {

        // ===== GIVEN ===== //

        Course course = Course.builder()
                .name(null)
                .teacher("Prof. Arnaldo")
                .build();

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> courseRepository.saveAndFlush(course))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe actualizar y retornar el curso cuando los campos requeridos estén presentes")
    void update_ShouldUpdateCourse_WhenRequiredFieldsArePresent() {

        // ===== GIVEN ===== //

        Course existingCourse = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        Course savedCourse = entityManager.persistAndFlush(existingCourse);
        entityManager.clear();

        savedCourse.setName("updated__Microservicios con Spring Boot");
        savedCourse.setTeacher("updated__Prof. Arnaldo");

        // ===== WHEN ===== //

        Course updatedCourse = courseRepository.save(savedCourse);
        entityManager.flush();
        entityManager.clear();

        Course foundCourse = courseRepository.findById(updatedCourse.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(updatedCourse).isNotNull();
        assertThat(updatedCourse.getId()).isEqualTo(savedCourse.getId());

        assertThat(foundCourse)
                .usingRecursiveComparison()
                .isEqualTo(updatedCourse);
    }

    @Test
    @DisplayName("Debe actualizar y retornar el curso cuando el profesor sea null (campo opcional)")
    void update_ShouldUpdateCourse_WhenTeacherIsNull() {

        // ===== GIVEN ===== //

        Course existingCourse = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        Course savedCourse = entityManager.persistAndFlush(existingCourse);
        entityManager.clear();

        savedCourse.setTeacher(null);

        // ===== WHEN ===== //

        Course updatedCourse = courseRepository.save(savedCourse);
        entityManager.flush();
        entityManager.clear();

        Course foundCourse = courseRepository.findById(updatedCourse.getId()).orElseThrow();

        // ===== THEN ===== //

        assertThat(updatedCourse).isNotNull();
        assertThat(updatedCourse.getTeacher()).isNull();

        assertThat(foundCourse)
                .usingRecursiveComparison()
                .isEqualTo(updatedCourse);
    }

    @Test
    @DisplayName("Debe lanzar (DataIntegrityViolationException) al actualizar cuando el nombre sea null")
    void update_ShouldThrowDataIntegrityViolationException_WhenNameToUpdateIsNull() {

        // ===== GIVEN ===== //

        Course existingCourse = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        Course savedCourse = entityManager.persistAndFlush(existingCourse);
        entityManager.clear();

        savedCourse.setName(null);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> courseRepository.saveAndFlush(savedCourse))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe eliminar el curso cuando exista")
    void delete_ShouldDeleteCourse_WhenCourseExists() {

        // ===== GIVEN ===== //

        Course course = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        Course savedCourse = entityManager.persistAndFlush(course);
        entityManager.clear();

        // ===== WHEN ===== //

        courseRepository.deleteById(savedCourse.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Course> deletedCourse = courseRepository.findById(savedCourse.getId());

        // ===== THEN ===== //

        assertThat(deletedCourse).isEmpty();
    }

    @Test
    @DisplayName("No debe lanzar excepción ni eliminar registros existentes cuando el curso no exista")
    void delete_ShouldDoNothing_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long nonExistingId = 999L;

        Course existingCourse = Course.builder()
                .name("Microservicios con Spring Boot")
                .teacher("Prof. Arnaldo")
                .build();

        entityManager.persistAndFlush(existingCourse);
        entityManager.clear();

        // ===== WHEN & THEN ===== //

        assertThatCode(() -> courseRepository.deleteById(nonExistingId))
                .doesNotThrowAnyException();

        entityManager.flush();

        // Verifica que los registros existentes en la tabla permanezcan intactos
        assertThat(courseRepository.count()).isEqualTo(1);
    }
}
