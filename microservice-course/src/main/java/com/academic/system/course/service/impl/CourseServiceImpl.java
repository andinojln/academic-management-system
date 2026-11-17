package com.academic.system.course.service.impl;

import com.academic.system.course.dto.CourseDTO;
import com.academic.system.course.entity.Course;
import com.academic.system.course.exception.ResourceNotFoundException;
import com.academic.system.course.mapper.CourseMapper;
import com.academic.system.course.repository.CourseRepository;
import com.academic.system.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findAll() {

        List<Course> courses = courseRepository.findAll();
        return courseMapper.toDTOList(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO findById(Long id) {

        return courseRepository.findById(id)
                .map(courseMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + id
                ));
    }

    @Override
    @Transactional
    public CourseDTO save(CourseDTO course) {

        Course courseToSave = courseMapper.toEntity(course);
        Course savedCourse = courseRepository.save(courseToSave);

        return courseMapper.toDTO(savedCourse);
    }

    @Override
    @Transactional
    public CourseDTO update(Long id, CourseDTO course) {

        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + id
                ));

        existingCourse.setName(course.name());
        existingCourse.setTeacher(course.teacher());

        Course updatedCourse = courseRepository.save(existingCourse);
        return courseMapper.toDTO(updatedCourse);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Curso no encontrado con el ID: " + id
            );
        }

        courseRepository.deleteById(id);
    }
}
