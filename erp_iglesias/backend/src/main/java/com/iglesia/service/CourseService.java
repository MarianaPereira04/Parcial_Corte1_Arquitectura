package com.iglesia.service;

import com.iglesia.Church;
import com.iglesia.ChurchRepository;
import com.iglesia.Course;
import com.iglesia.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final ChurchRepository churchRepository;

    public CourseService(CourseRepository courseRepository, ChurchRepository churchRepository) {
        this.courseRepository = courseRepository;
        this.churchRepository = churchRepository;
    }

    public Course create(String name, String description, BigDecimal price) {
        Church church = requireChurch();
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        course.setPrice(price);
        course.setChurch(church);
        courseRepository.save(course);
        return course;
    }

    public List<Course> list() {
        Church church = requireChurch();
        return courseRepository.findAllByChurchId(church.getId());
    }

    private Church requireChurch() {
        return churchRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));
    }
}
