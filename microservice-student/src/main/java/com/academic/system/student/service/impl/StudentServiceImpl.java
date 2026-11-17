package com.academic.system.student.service.impl;

import com.academic.system.student.dto.StudentDTO;
import com.academic.system.student.entity.Student;
import com.academic.system.student.exception.ConflictException;
import com.academic.system.student.exception.ResourceNotFoundException;
import com.academic.system.student.mapper.StudentMapper;
import com.academic.system.student.repository.StudentRepository;
import com.academic.system.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {

        List<Student> students = studentRepository.findAll();
        return studentMapper.toDTOList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO findById(Long id) {

        return studentRepository.findById(id)
                .map(studentMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con el ID: " + id
                ));
    }

    @Override
    @Transactional
    public StudentDTO save(StudentDTO student) {

        if (studentRepository.existsByEmail(student.email())) {
            throw new ConflictException(
                    "El email '" + student.email() + "' ya se encuentra registrado"
            );
        }

        Student studentToSave = studentMapper.toEntity(student);
        Student savedStudent = studentRepository.save(studentToSave);
        return studentMapper.toDTO(savedStudent);
    }

    @Override
    @Transactional
    public StudentDTO update(Long id, StudentDTO student) {

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con el ID: " + id)
                );

        if (!existingStudent.getEmail().equals(student.email()) &&
                studentRepository.existsByEmail(student.email())) {
            throw new ConflictException(
                    "El email '" + student.email() + "' ya se encuentra registrado"
            );
        }

        existingStudent.setName(student.name());
        existingStudent.setLastName(student.lastName());
        existingStudent.setEmail(student.email());

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDTO(updatedStudent);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Estudiante no encontrado con el ID: " + id
            );
        }

        studentRepository.deleteById(id);
    }
}
