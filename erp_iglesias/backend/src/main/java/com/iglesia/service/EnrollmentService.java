package com.iglesia.service;

import com.iglesia.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchRepository churchRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             PersonRepository personRepository,
                             CourseRepository courseRepository,
                             PaymentRepository paymentRepository,
                             ChurchRepository churchRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.paymentRepository = paymentRepository;
        this.churchRepository = churchRepository;
    }

    public record EnrollmentWithPayment(Enrollment enrollment, Payment payment) {}

    public EnrollmentWithPayment create(Long personId, Long courseId) {
        Church church = requireChurch();
        Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));

        if (!person.getChurch().getId().equals(church.getId())
            || !course.getChurch().getId().equals(church.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos no pertenecen a la iglesia");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setPerson(person);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDIENTE);
        enrollmentRepository.save(enrollment);

        Payment payment = new Payment();
        payment.setType(PaymentType.INSCRIPCION_CURSO);
        payment.setAmount(course.getPrice());
        payment.setReferenceId(enrollment.getId());
        paymentRepository.save(payment);

        enrollment.setPaymentId(payment.getId());
        enrollmentRepository.save(enrollment);

        return new EnrollmentWithPayment(enrollment, payment);
    }

    public List<EnrollmentWithPayment> list() {
        Church church = requireChurch();
        return enrollmentRepository.findAllByPersonChurchId(church.getId())
            .stream()
            .map(enrollment -> {
                Payment payment = null;
                if (enrollment.getPaymentId() != null) {
                    payment = paymentRepository.findById(enrollment.getPaymentId()).orElse(null);
                }
                return new EnrollmentWithPayment(enrollment, payment);
            })
            .collect(Collectors.toList());
    }

    private Church requireChurch() {
        return churchRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));
    }
}
