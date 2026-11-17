package com.academic.system.enrollment.mapper;

import com.academic.system.enrollment.dto.response.EnrollmentDTO;
import com.academic.system.enrollment.entity.Enrollment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    EnrollmentDTO toDTO(Enrollment enrollment);

    Enrollment toEntity(EnrollmentDTO enrollment);
}
