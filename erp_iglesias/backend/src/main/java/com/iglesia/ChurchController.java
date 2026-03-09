package com.iglesia;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import com.iglesia.service.ChurchService;

@RestController
@RequestMapping("/api/church")
public class ChurchController {
    private final ChurchService churchService;

    public ChurchController(ChurchService churchService) {
        this.churchService = churchService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ChurchResponse create(@Valid @RequestBody ChurchRequest request) {
        Church church = churchService.create(request.name(), request.address());
        return ChurchResponse.from(church);
    }

    @GetMapping
    public ChurchResponse get() {
        return ChurchResponse.from(churchService.getChurch());
    }

    public record ChurchRequest(
        @NotBlank String name,
        String address
    ) {}

    public record ChurchResponse(
        Long id,
        String name,
        String address
    ) {
        public static ChurchResponse from(Church church) {
            return new ChurchResponse(church.getId(), church.getName(), church.getAddress());
        }
    }
}
