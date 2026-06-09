package com.academic.system.student.mapper;

import com.academic.system.student.dto.StudentDTO;
import com.academic.system.student.entity.Student;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentDTO toDTO(Student student);

    List<StudentDTO> toDTOList(List<Student> students);

    Student toEntity(StudentDTO studentDTO);
}
