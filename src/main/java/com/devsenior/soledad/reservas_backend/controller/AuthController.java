package com.devsenior.soledad.reservas_backend.controller;

import com.devsenior.soledad.reservas_backend.dto.AuthResponseDTO;
import com.devsenior.soledad.reservas_backend.dto.LoginRequestDTO;
import com.devsenior.soledad.reservas_backend.dto.RegisterRequestDTO;
import com.devsenior.soledad.reservas_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new company and its administrator user using the public slug provided by the client.
     *
     * @param request validated registration payload
     * @return authentication response for the newly created tenant
     */
    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Authenticates an existing user.
     *
     * @param request validated login payload
     * @return authentication response with the issued token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
