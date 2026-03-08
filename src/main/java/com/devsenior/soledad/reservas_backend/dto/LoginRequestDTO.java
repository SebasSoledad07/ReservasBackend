package com.devsenior.soledad.reservas_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para login de usuario.
 */
public record LoginRequestDTO(
        @NotBlank
        String username,
        @NotBlank
        String password
) {}

