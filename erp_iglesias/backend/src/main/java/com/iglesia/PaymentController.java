package com.iglesia;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import com.iglesia.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public ApiResponse<java.util.List<PaymentResponse>> list(@RequestParam(name = "status", required = false) PaymentStatus status) {
        java.util.List<Payment> payments = paymentService.list(status);
        return ApiResponse.success("Lista de pagos", payments.stream().map(PaymentResponse::from).toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/confirm")
    public ApiResponse<PaymentResponse> confirm(@PathVariable Long id) {
        Payment payment = paymentService.confirm(id);
        return ApiResponse.success("Pago confirmado", PaymentResponse.from(payment));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/fail")
    public ApiResponse<PaymentResponse> fail(@PathVariable Long id) {
        Payment payment = paymentService.fail(id);
        return ApiResponse.success("Pago marcado como fallido", PaymentResponse.from(payment));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping("/{id}/retry")
    public ApiResponse<PaymentResponse> retry(@PathVariable Long id) {
        Payment payment = paymentService.retry(id);
        return ApiResponse.success("Pago reintentado", PaymentResponse.from(payment));
    }

    public record PaymentResponse(
        Long id,
        String type,
        String status,
        String amount,
        int attempts,
        Long referenceId
    ) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                payment.getId(),
                payment.getType().name(),
                payment.getStatus().name(),
                payment.getAmount().toPlainString(),
                payment.getAttempts(),
                payment.getReferenceId()
            );
        }
    }
}
