package com.devsenior.soledad.reservas_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para registro de compañía y admin.
 */
public record RegisterRequestDTO(
        @NotBlank
        @Size(min = 3, max = 50)
        String companyName,
        @NotBlank
        @Size(min = 3, max = 50)
        String username,
        @NotBlank
        @Size(min = 6)
        String password,
        String email
) {}

