package com.iglesia.service;

import com.iglesia.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferingService {
    private final OfferingRepository offeringRepository;
    private final PersonRepository personRepository;
    private final PaymentRepository paymentRepository;
    private final ChurchRepository churchRepository;

    public OfferingService(OfferingRepository offeringRepository,
                           PersonRepository personRepository,
                           PaymentRepository paymentRepository,
                           ChurchRepository churchRepository) {
        this.offeringRepository = offeringRepository;
        this.personRepository = personRepository;
        this.paymentRepository = paymentRepository;
        this.churchRepository = churchRepository;
    }

    public record OfferingWithPayment(Offering offering, Payment payment) {}

    public OfferingWithPayment create(Long personId, java.math.BigDecimal amount, String concept) {
        Church church = requireChurch();
        Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));

        if (!person.getChurch().getId().equals(church.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Persona no pertenece a la iglesia");
        }

        Offering offering = new Offering();
        offering.setPerson(person);
        offering.setAmount(amount);
        offering.setConcept(concept);
        offering.setStatus(OfferingStatus.PENDIENTE);
        offeringRepository.save(offering);

        Payment payment = new Payment();
        payment.setType(PaymentType.OFRENDA);
        payment.setAmount(amount);
        payment.setReferenceId(offering.getId());
        paymentRepository.save(payment);

        offering.setPaymentId(payment.getId());
        offeringRepository.save(offering);

        return new OfferingWithPayment(offering, payment);
    }

    public List<OfferingWithPayment> list() {
        Church church = requireChurch();
        return offeringRepository.findAllByPersonChurchId(church.getId())
            .stream()
            .map(offering -> {
                Payment payment = null;
                if (offering.getPaymentId() != null) {
                    payment = paymentRepository.findById(offering.getPaymentId()).orElse(null);
                }
                return new OfferingWithPayment(offering, payment);
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
