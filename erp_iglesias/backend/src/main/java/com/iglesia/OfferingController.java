package com.iglesia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import com.iglesia.service.OfferingService;

@RestController
@RequestMapping("/api/offerings")
public class OfferingController {
    private final OfferingService offeringService;

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public OfferingResponse create(@RequestBody OfferingRequest request) {
        OfferingService.OfferingWithPayment result = offeringService.create(request.personId(), request.amount(), request.concept());
        return OfferingResponse.from(result.offering(), result.payment());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public List<OfferingResponse> list() {
        return offeringService.list()
            .stream()
            .map(op -> OfferingResponse.from(op.offering(), op.payment()))
            .toList();
    }

    // requireChurch moved to OfferingService

    public record OfferingRequest(
        @NotNull Long personId,
        @NotNull BigDecimal amount,
        @NotBlank String concept
    ) {}

    public record OfferingResponse(
        Long id,
        Long personId,
        String personName,
        String concept,
        String amount,
        String status,
        Long paymentId,
        String paymentStatus
    ) {
        public static OfferingResponse from(Offering offering, Payment payment) {
            String personName = offering.getPerson().getFirstName() + " " + offering.getPerson().getLastName();
            String paymentStatus = payment == null ? null : payment.getStatus().name();
            return new OfferingResponse(
                offering.getId(),
                offering.getPerson().getId(),
                personName,
                offering.getConcept(),
                offering.getAmount().toPlainString(),
                offering.getStatus().name(),
                offering.getPaymentId(),
                paymentStatus
            );
        }
    }
}
