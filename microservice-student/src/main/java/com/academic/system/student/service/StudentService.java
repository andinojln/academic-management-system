package com.academic.system.student.service;

import com.academic.system.student.dto.StudentDTO;

import java.util.List;

public interface StudentService {

    List<StudentDTO> findAll();

    StudentDTO findById(Long id);

    StudentDTO save(StudentDTO studentDTO);

    StudentDTO update(Long id, StudentDTO studentDTO);

    void delete(Long id);
}
