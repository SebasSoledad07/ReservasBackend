package com.devsenior.soledad.reservas_backend.dto;

/**
 * Response DTO for authentication responses.
 *
 * @param token the JWT token
 * @param companyId the id of the company the user belongs to
 * @param username  the authenticated username
 * @param role      the user role
 * @param slug      the public company slug
 */
public record AuthResponseDTO(String token, Long companyId, String username, String role, String slug) {
}
