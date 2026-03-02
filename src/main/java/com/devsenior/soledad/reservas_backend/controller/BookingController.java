package com.devsenior.soledad.reservas_backend.controller;

import com.devsenior.soledad.reservas_backend.dto.BookingRequest;
import com.devsenior.soledad.reservas_backend.dto.BookingResponse;
import com.devsenior.soledad.reservas_backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for bookings.
 */
@RestController
@RequestMapping("/reservas")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Lists all bookings.
     *
     * @return list of booking responses
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> listAll() {
        List<BookingResponse> list = bookingService.listAllBookings();
        return ResponseEntity.ok(list);
    }

    /**
     * Creates a new booking.
     *
     * @param request the booking request payload
     * @return created booking response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cancels a booking by id.
     *
     * @param id booking identifier
     * @return no content if cancelled
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        bookingService.cancelBookingById(id);
        return ResponseEntity.noContent().build();
    }
}

