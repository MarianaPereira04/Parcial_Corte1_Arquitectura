package com.iglesia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import com.iglesia.service.PersonService;

@RestController
@RequestMapping("/api/people")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @PostMapping
    public ApiResponse<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
        Person person = personService.create(request.firstName(), request.lastName(), request.document(), request.phone(), request.email());
        return ApiResponse.success("Persona creada", PersonResponse.from(person));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @GetMapping
    public ApiResponse<java.util.List<PersonResponse>> list() {
        return ApiResponse.success("Lista de personas", personService.list()
            .stream()
            .map(PersonResponse::from)
            .toList());
    }

    // requireChurch moved to PersonService

    public record PersonRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String document,
        String phone,
        @Email String email
    ) {}

    public record PersonResponse(
        Long id,
        String firstName,
        String lastName,
        String document,
        String phone,
        String email
    ) {
        public static PersonResponse from(Person person) {
            return new PersonResponse(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getDocument(),
                person.getPhone(),
                person.getEmail()
            );
        }
    }
}
