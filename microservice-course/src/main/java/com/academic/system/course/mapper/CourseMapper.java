package com.academic.system.course.mapper;

import com.academic.system.course.dto.CourseDTO;
import com.academic.system.course.entity.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDTO(Course course);

    List<CourseDTO> toDTOList(List<Course> courses);

    Course toEntity(CourseDTO course);
}
