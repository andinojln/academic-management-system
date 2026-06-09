package com.academic.system.student.service.impl;

import com.academic.system.student.dto.StudentDTO;
import com.academic.system.student.entity.Student;
import com.academic.system.student.exception.BadRequestException;
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
                        "Estudiante no encontrado con el id: " + id
                ));
    }

    @Override
    @Transactional
    public StudentDTO save(StudentDTO studentDTO) {
        if (studentRepository.existsByEmail(studentDTO.email())) {
            throw new BadRequestException(
                    "El email '" + studentDTO.email() + "' ya se encuentra registrado"
            );
        }
        Student student = studentMapper.toEntity(studentDTO);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDTO(savedStudent);
    }

    @Override
    @Transactional
    public StudentDTO update(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con el id: " + id
                ));
        if (!student.getEmail().equals(studentDTO.email()) &&
                studentRepository.existsByEmail(studentDTO.email())) {
            throw new BadRequestException(
                    "El email '" + studentDTO.email() + "' ya se encuentra registrado"
            );
        }
        student.setName(studentDTO.name());
        student.setLastName(studentDTO.lastName());
        student.setEmail(studentDTO.email());
        Student updatedStudent = studentRepository.save(student);
        return studentMapper.toDTO(updatedStudent);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Estudiante no encontrado con el id: " + id
            );
        }
        studentRepository.deleteById(id);
    }
}
