package com.devsenior.soledad.reservas_backend.controller;

import com.devsenior.soledad.reservas_backend.dto.BookingPublicRequestDTO;
import com.devsenior.soledad.reservas_backend.dto.BookingResponseDTO;
import com.devsenior.soledad.reservas_backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public endpoints for creating bookings without authentication.
 */
@RestController
@RequestMapping("/publico")
public class PublicController {

    private final BookingService bookingService;

    public PublicController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Creates a booking for the company identified by slug. This endpoint is public and does not require authentication.
     *
     * @param slug company slug
     * @param request booking request payload
     * @return created booking
     */
    @PostMapping("/{slug}/reservas")
    public ResponseEntity<BookingResponseDTO> createPublicBooking(@PathVariable String slug, @Valid @RequestBody BookingPublicRequestDTO request) {
        BookingResponseDTO response = bookingService.createPublicBooking(slug, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

