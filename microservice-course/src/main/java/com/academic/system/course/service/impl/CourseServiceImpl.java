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
                        "Curso no encontrado con el id: " + id
                ));
    }

    @Override
    @Transactional
    public CourseDTO save(CourseDTO courseDTO) {
        Course course = courseMapper.toEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDTO(savedCourse);
    }

    @Override
    @Transactional
    public CourseDTO update(Long id, CourseDTO courseDTO) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setName(courseDTO.name());
                    course.setTeacher(courseDTO.teacher());
                    Course updatedCourse = courseRepository.save(course);
                    return courseMapper.toDTO(updatedCourse);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso no encontrado con el ID: " + id
                ));
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
