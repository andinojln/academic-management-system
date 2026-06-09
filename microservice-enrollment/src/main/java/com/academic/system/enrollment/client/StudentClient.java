package com.academic.system.enrollment.client;

import com.academic.system.enrollment.dto.client.StudentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-student", path = "/api/students")
public interface StudentClient {

    @GetMapping("/{id}")
    StudentDTO findById(@PathVariable Long id);
}
