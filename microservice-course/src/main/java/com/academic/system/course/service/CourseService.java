package com.academic.system.course.service;

import com.academic.system.course.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    List<CourseDTO> findAll();

    CourseDTO findById(Long id);

    CourseDTO save(CourseDTO course);

    CourseDTO update(Long id, CourseDTO course);

    void delete(Long id);
}
