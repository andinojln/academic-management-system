package com.academic.system.course.service;

import com.academic.system.course.dto.CourseDTO;
import com.academic.system.course.entity.Course;
import com.academic.system.course.exception.ResourceNotFoundException;
import com.academic.system.course.mapper.CourseMapper;
import com.academic.system.course.repository.CourseRepository;
import com.academic.system.course.service.impl.CourseServiceImpl;
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
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    // ===== Patrón utilizado para los Tests Unitarios (Service): Given_When_Then ===== //

    @Test
    @DisplayName("Debe retornar la lista de cursos cuando existan registros")
    void findAll_ShouldReturnCoursesList_WhenRecordsExist() {

        // ===== GIVEN ===== //

        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("Microservicios con Spring Boot");
        course1.setTeacher("Prof. Arnaldo");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Programación Reactiva");
        course2.setTeacher("Dra. Marina");

        List<Course> courses = List.of(course1, course2);

        CourseDTO expectedCourse1 = new CourseDTO(
                course1.getId(),
                course1.getName(),
                course1.getTeacher());

        CourseDTO expectedCourse2 = new CourseDTO(
                course2.getId(),
                course2.getName(),
                course2.getTeacher());

        List<CourseDTO> expectedCourses = List.of(expectedCourse1, expectedCourse2);

        when(courseRepository.findAll()).thenReturn(courses);
        when(courseMapper.toDTOList(courses)).thenReturn(expectedCourses);

        // ===== WHEN ===== //

        List<CourseDTO> coursesResult = courseService.findAll();

        // ===== THEN ===== //

        assertThat(coursesResult)
                .isNotNull()
                .hasSize(2)
                .containsExactlyElementsOf(expectedCourses);

        verify(courseRepository, times(1)).findAll();
        verify(courseMapper, times(1)).toDTOList(courses);
    }

    @Test
    @DisplayName("Debe retornar la lista vacía cuando no existan registros")
    void findAll_ShouldReturnEmptyList_WhenNoRecordsExist() {

        // ===== GIVEN ===== //

        List<Course> emptyList = List.of();

        when(courseRepository.findAll()).thenReturn(emptyList);
        when(courseMapper.toDTOList(emptyList)).thenReturn(List.of());

        // ===== WHEN ===== //

        List<CourseDTO> coursesResult = courseService.findAll();

        // ===== THEN ===== //

        assertThat(coursesResult)
                .isNotNull()
                .isEmpty();

        verify(courseRepository, times(1)).findAll();
        verify(courseMapper, times(1)).toDTOList(emptyList);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe retornar el curso cuando exista")
    void findById_ShouldReturnCourse_WhenCourseExists() {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        Course course = new Course();
        course.setId(courseId);
        course.setName("Microservicios con Spring Boot");
        course.setTeacher("Prof. Arnaldo");

        CourseDTO expectedCourse = new CourseDTO(
                course.getId(),
                course.getName(),
                course.getTeacher());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toDTO(course)).thenReturn(expectedCourse);

        // ===== WHEN ===== //

        CourseDTO courseResult = courseService.findById(courseId);

        // ===== THEN ===== //

        assertThat(courseResult)
                .isNotNull()
                .isEqualTo(expectedCourse);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseMapper, times(1)).toDTO(course);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el curso no exista")
    void findById_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> courseService.findById(courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado con el ID: " + courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verifyNoInteractions(courseMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe guardar y retornar el curso")
    void save_ShouldReturnCourse() {

        // ===== GIVEN ===== //

        CourseDTO inputCourse = new CourseDTO(
                null,
                "Microservicios con Spring Boot",
                "Prof. Arnaldo");

        Course courseToSave = new Course();
        courseToSave.setName(inputCourse.name());
        courseToSave.setTeacher(inputCourse.teacher());

        Course savedCourse = new Course();
        savedCourse.setId(1L);
        savedCourse.setName(inputCourse.name());
        savedCourse.setTeacher(inputCourse.teacher());

        CourseDTO expectedCourse = new CourseDTO(
                savedCourse.getId(),
                savedCourse.getName(),
                savedCourse.getTeacher());

        when(courseMapper.toEntity(inputCourse)).thenReturn(courseToSave);
        when(courseRepository.save(courseToSave)).thenReturn(savedCourse);
        when(courseMapper.toDTO(savedCourse)).thenReturn(expectedCourse);

        // ===== WHEN ===== //

        CourseDTO courseResult = courseService.save(inputCourse);

        // ===== THEN ===== //

        assertThat(courseResult)
                .isNotNull()
                .isEqualTo(expectedCourse);

        verify(courseMapper, times(1)).toEntity(inputCourse);
        verify(courseRepository, times(1)).save(courseToSave);
        verify(courseMapper, times(1)).toDTO(savedCourse);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe actualizar y retornar el curso cuando exista")
    void update_ShouldReturnCourse_WhenCourseExists() {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        Course existingCourse = new Course();
        existingCourse.setId(courseId);
        existingCourse.setName("Microservicios con Spring Boot");
        existingCourse.setTeacher("Prof. Arnaldo");

        CourseDTO inputCourse = new CourseDTO(
                courseId,
                "updated__Microservicios con Spring Boot",
                "updated__Prof. Arnaldo");

        CourseDTO expectedCourse = new CourseDTO(
                inputCourse.id(),
                inputCourse.name(),
                inputCourse.teacher());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(existingCourse)).thenReturn(existingCourse);
        when(courseMapper.toDTO(existingCourse)).thenReturn(expectedCourse);

        // ===== WHEN ===== //

        CourseDTO courseResult = courseService.update(courseId, inputCourse);

        // ===== THEN ===== //

        assertThat(courseResult)
                .isNotNull()
                .isEqualTo(expectedCourse);

        assertThat(existingCourse.getName()).isEqualTo(inputCourse.name());
        assertThat(existingCourse.getTeacher()).isEqualTo(inputCourse.teacher());

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(existingCourse);
        verify(courseMapper, times(1)).toDTO(existingCourse);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el curso no exista")
    void update_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        CourseDTO inputCourse = new CourseDTO(
                courseId,
                "updated__Microservicios con Spring Boot",
                "updated__Prof. Arnaldo");

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> courseService.update(courseId, inputCourse))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado con el ID: " + courseId);

        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any(Course.class));
        verifyNoInteractions(courseMapper);
    }

    // ====================================== //

    @Test
    @DisplayName("Debe eliminar el curso cuando exista")
    void delete_ShouldDeleteCourse_WhenCourseExists() {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        when(courseRepository.existsById(courseId)).thenReturn(true);

        // ===== WHEN & THEN ===== //

        assertThatNoException().isThrownBy(() -> courseService.delete(courseId));

        verify(courseRepository, times(1)).existsById(courseId);
        verify(courseRepository, times(1)).deleteById(courseId);
        verifyNoInteractions(courseMapper);
    }

    @Test
    @DisplayName("Debe lanzar (ResourceNotFoundException) cuando el curso no exista")
    void delete_ShouldThrowResourceNotFoundException_WhenCourseDoesNotExist() {

        // ===== GIVEN ===== //

        Long courseId = 1L;

        when(courseRepository.existsById(courseId)).thenReturn(false);

        // ===== WHEN & THEN ===== //

        assertThatThrownBy(() -> courseService.delete(courseId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado con el ID: " + courseId);

        verify(courseRepository, times(1)).existsById(courseId);
        verify(courseRepository, never()).deleteById(anyLong());
        verifyNoInteractions(courseMapper);
    }
}
