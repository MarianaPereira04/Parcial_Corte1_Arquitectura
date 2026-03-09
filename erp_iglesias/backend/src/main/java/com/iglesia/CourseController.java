package com.iglesia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import com.iglesia.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CourseResponse create(@Valid @RequestBody CourseRequest request) {
        Course course = courseService.create(request.name(), request.description(), request.price());
        return CourseResponse.from(course);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<CourseResponse> list() {
        return courseService.list()
            .stream()
            .map(CourseResponse::from)
            .toList();
    }

    // requireChurch moved to CourseService

    public record CourseRequest(
        @NotBlank String name,
        String description,
        @NotNull BigDecimal price
    ) {}

    public record CourseResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean active
    ) {
        public static CourseResponse from(Course course) {
            return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getPrice(),
                course.isActive()
            );
        }
    }
}
