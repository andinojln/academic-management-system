package com.academic.system.enrollment.client;

import com.academic.system.enrollment.dto.client.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-course", path = "/api/courses")
public interface CourseClient {

    @GetMapping("/{id}")
    CourseDTO findById(@PathVariable Long id);
}
