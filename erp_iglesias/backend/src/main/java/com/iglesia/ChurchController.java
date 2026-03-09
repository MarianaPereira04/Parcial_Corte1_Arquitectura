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
    public ApiResponse<ChurchResponse> create(@Valid @RequestBody ChurchRequest request) {
        Church church = churchService.create(request.name(), request.address());
        return ApiResponse.success("Iglesia creada", ChurchResponse.from(church));
    }

    @GetMapping
    public ApiResponse<ChurchResponse> get() {
        return ApiResponse.success("Iglesia encontrada", ChurchResponse.from(churchService.getChurch()));
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
