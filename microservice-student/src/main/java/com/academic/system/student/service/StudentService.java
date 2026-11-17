package com.academic.system.student.service;

import com.academic.system.student.dto.StudentDTO;

import java.util.List;

public interface StudentService {

    List<StudentDTO> findAll();

    StudentDTO findById(Long id);

    StudentDTO save(StudentDTO student);

    StudentDTO update(Long id, StudentDTO student);

    void delete(Long id);
}
