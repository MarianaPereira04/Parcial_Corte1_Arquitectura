package com.iglesia;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import com.iglesia.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AuthService.AuthResult result = authService.login(request.email(), request.password());
        return new LoginResponse(result.token(), result.email(), result.role());
    }


    public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
    ) {}

    public record LoginResponse(
        String token,
        String email,
        String role
    ) {}

}
