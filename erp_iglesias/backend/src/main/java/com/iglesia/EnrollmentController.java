package com.iglesia;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import com.iglesia.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public EnrollmentResponse create(@RequestBody EnrollmentRequest request) {
        EnrollmentService.EnrollmentWithPayment result = enrollmentService.create(request.personId(), request.courseId());
        return EnrollmentResponse.from(result.enrollment(), result.payment());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<EnrollmentResponse> list() {
        return enrollmentService.list()
            .stream()
            .map(ep -> EnrollmentResponse.from(ep.enrollment(), ep.payment()))
            .toList();
    }

    // requireChurch moved to EnrollmentService

    public record EnrollmentRequest(
        @NotNull Long personId,
        @NotNull Long courseId
    ) {}

    public record EnrollmentResponse(
        Long id,
        Long personId,
        String personName,
        Long courseId,
        String courseName,
        String status,
        Long paymentId,
        String paymentStatus
    ) {
        public static EnrollmentResponse from(Enrollment enrollment, Payment payment) {
            String personName = enrollment.getPerson().getFirstName() + " " + enrollment.getPerson().getLastName();
            String paymentStatus = payment == null ? null : payment.getStatus().name();
            return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getPerson().getId(),
                personName,
                enrollment.getCourse().getId(),
                enrollment.getCourse().getName(),
                enrollment.getStatus().name(),
                enrollment.getPaymentId(),
                paymentStatus
            );
        }
    }
}
