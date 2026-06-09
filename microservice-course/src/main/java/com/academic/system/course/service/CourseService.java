package com.academic.system.course.service;

import com.academic.system.course.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    List<CourseDTO> findAll();

    CourseDTO findById(Long id);

    CourseDTO save(CourseDTO courseDTO);

    CourseDTO update(Long id, CourseDTO courseDTO);

    void delete(Long id);
}
